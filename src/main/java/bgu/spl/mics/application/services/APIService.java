package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.Broadcasts.TerminateBroadcast;
import bgu.spl.mics.application.Broadcasts.TickBroadcast;
import bgu.spl.mics.application.Events.BookOrderEvent;
import bgu.spl.mics.application.Events.DeliveryEvent;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.Pair;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;


/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService {

	private int currentTick;
	private ConcurrentHashMap<Integer, ConcurrentLinkedQueue<String>> tickBooksNamesMap;
	private ConcurrentHashMap<BookOrderEvent,Integer> eventToTickTimeMap;
	private Customer customer;
	private CountDownLatch countDownLatch;


	public APIService(Customer customer, int i,CountDownLatch countDownLatch) {
		super("api "+i);
		this.countDownLatch=countDownLatch;
		this.customer=customer;
		tickBooksNamesMap=new ConcurrentHashMap<>(); //do not forget to init the queue on adding new key !!!
		eventToTickTimeMap=new ConcurrentHashMap<>();
		initOrderSchedule();

	}

	@Override
	protected void initialize() {
		countDownLatch.countDown();
		subscribeBroadcast(TickBroadcast.class,(TickBroadcast tickBroadcast)->{
																				//System.out.println(super.getName()+" GOT TICK "+tickBroadcast.getCurClockTick());
																				currentTick=tickBroadcast.getCurClockTick();
																				//checks if there is book to order in that tick
																				ConcurrentLinkedQueue<String> currentTickBooks=tickBooksNamesMap.get(currentTick);
																				if(currentTickBooks!=null) { //this customer has books to order on this schedule
																					for (String bookName : currentTickBooks) {
																						System.out.println(getName()+" ["+customer.getName()+"] "+" SENT BOE FOR : "+bookName);
																						BookOrderEvent bookOrderEvent=new BookOrderEvent(bookName, customer,this);
																						//System.out.println(customer.getName()+" wants to order "+bookName);
																						eventToTickTimeMap.put(bookOrderEvent,currentTick);
																						System.out.println(customer.getName()+": "+eventToTickTimeMap.size());
																						Future<OrderReceipt>futureOrderRecipt=sendEvent(bookOrderEvent);
																						OrderReceipt futureResult=futureOrderRecipt.get();

																						if(futureResult!=null) {
																							System.out.println(getName()+" ["+customer.getName()+"] "+ "GOT RECEIPT FOR: "+bookName);
																							DeliveryEvent deliveryEvent=new DeliveryEvent(customer);
																							sendEvent(deliveryEvent);
																						}
																						else {
																							System.out.println(getName()+" ["+customer.getName()+"] "+ "failed get RECEIPT for: "+bookName);
																						}
																						tickBooksNamesMap.remove(currentTick);	}}

																									});

	subscribeBroadcast(TerminateBroadcast.class,(TerminateBroadcast)->{terminate();});
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

	public Integer getOrderdBookTick(BookOrderEvent bookOrderEvent) {
		return eventToTickTimeMap.get(bookOrderEvent);
	}
}
