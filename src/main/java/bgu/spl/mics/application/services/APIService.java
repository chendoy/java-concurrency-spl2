package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.Broadcasts.FiftyPercentDiscount;
import bgu.spl.mics.application.Broadcasts.TickBroadcast;
import bgu.spl.mics.application.Events.BookOrderEvent;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.Pair;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService implements Callback<Message> {

	private int currentTick;

	private ConcurrentHashMap<Integer, ConcurrentLinkedQueue<String>> tickBooksNamesMap;
	private ConcurrentHashMap<BookOrderEvent,Integer> eventToTickTimeMap;
	private Customer customer;

	public APIService(Customer customer,int i) {
		super("api "+i);
		initialize();
	}

	@Override
	protected void initialize() {

		tickBooksNamesMap=new ConcurrentHashMap<>(); //do not forget to init the queue on adding new key !!!
		eventToTickTimeMap=new ConcurrentHashMap<>();
		initOrderSchedule();
		MessageBusImpl.getInstance().register(this);
		MessageBusImpl.getInstance().subscribeBroadcast(FiftyPercentDiscount.class,this);
		MessageBusImpl.getInstance().subscribeBroadcast(TickBroadcast.class,this);
	}

	private void initOrderSchedule() {
		List<Pair<String,Integer>> cusOrderSchdule=customer.getOrderSchedule();
		for(int i=0;i<cusOrderSchdule.size();i=i+1) {
			if(!tickBooksNamesMap.containsKey(cusOrderSchdule.get(i).getValue())) {
				ConcurrentLinkedQueue<String> booksInTick=new ConcurrentLinkedQueue<>();
				booksInTick.add(cusOrderSchdule.get(i).getKey());
				tickBooksNamesMap.put(cusOrderSchdule.get(i).getValue(),booksInTick);
			} else {
				tickBooksNamesMap.get(cusOrderSchdule.get(i).getValue()).add(cusOrderSchdule.get(i).getKey());
			}
		}

	}

	@Override
	public void call(Message c) {
		if (c instanceof TickBroadcast) {
			currentTick=((TickBroadcast) c).getCurClockTick();
			//checks if there is book to order in that tick
			ConcurrentLinkedQueue<String> currentTickBooks=tickBooksNamesMap.get(currentTick);
			if(currentTickBooks!=null) { //this customer has books to order on this schedule
				for (String bookName : currentTickBooks) {
					BookOrderEvent bookOrderEvent =new BookOrderEvent(bookName);
					MessageBusImpl.getInstance().sendEvent(bookOrderEvent);
					eventToTickTimeMap.put(bookOrderEvent,currentTick);
				}
				tickBooksNamesMap.remove(currentTick);
			}
		}

	}

	public Integer getOrderdBookTick(BookOrderEvent bookOrderEvent) {
		return eventToTickTimeMap.get(bookOrderEvent);
	}
}
