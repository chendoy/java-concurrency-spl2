package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.Broadcasts.TickBroadcast;
import bgu.spl.mics.application.Events.BookOrderEvent;
import bgu.spl.mics.application.Events.CheckAvailability;
import bgu.spl.mics.application.Events.DeliveryEvent;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService implements Callback<Message> {

	private int curBookId;
	private int curTick=1;
	private int startProcessTickTime;
	private ConcurrentHashMap<Message, Pair<Integer,Integer>> MessageToStartEndTimes;
	private MoneyRegister moneyRegister;

	public SellingService(int i,MoneyRegister moneyRegister) {
		super("selling "+i);
		this.moneyRegister=moneyRegister;
		initialize();
	}

	@Override
	protected void initialize() {
		MessageToStartEndTimes=new ConcurrentHashMap<>();
		startProcessTickTime=-1;
		MessageBusImpl.getInstance().register(this);
		MessageBusImpl.getInstance().subscribeBroadcast(TickBroadcast.class,this);
		MessageBusImpl.getInstance().subscribeEvent(BookOrderEvent.class,this);
	}

	@Override
	public void call(Message c) {
		if(c instanceof BookOrderEvent) {
			MessageToStartEndTimes.put(c,new Pair(curTick,null));
			String bookName=((BookOrderEvent)c).getBookName();
			BookOrderEvent bookEvent=(BookOrderEvent)c;
			CheckAvailability checkAvailability=new CheckAvailability(bookName);
			Future<CheckAvailability> futureAvailability=MessageBusImpl.getInstance().sendEvent(checkAvailability);
			CheckAvailability avilablity=futureAvailability.get();
			if(avilablity.getAvailable()!=-1) {
				Customer customerToCharge=bookEvent.getCustomer();
				int price=avilablity.getAvailable();
				boolean CanBeCharged=customerToCharge.canChargeCreditCard(price);
				if(CanBeCharged) {
					OrderResult takeOrder=Inventory.getInstance().take(bookName);
					if(takeOrder==OrderResult.SUCCESSFULLY_TAKEN) //book is available+canBeCharged+successfully_taken
					{
						OrderReceipt newOrderReceipt=new OrderReceipt(bookName,price,customerToCharge,getStartProcessTickTime(bookEvent),getName(),bookEvent.getEventTick(),curTick);
						moneyRegister.file(newOrderReceipt);
						complete((Event)c,newOrderReceipt);
					}
						else
							complete((Event)c,null);
				}
				else {
					complete((Event)c,null);
				}
			}
			else {
				complete((Event)c,null);
			}

		}
		else if (c instanceof TickBroadcast) {
			curTick=((TickBroadcast) c).getCurClockTick();
		}
	}
	public int getStartProcessTickTime(BookOrderEvent order) {
		return MessageToStartEndTimes.get(order).getKey();

	}
}