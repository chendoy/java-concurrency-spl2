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
public class SellingService extends MicroService {

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
		subscribeBroadcast(TickBroadcast.class,(TickBroadcast tickBroadcast)->curTick=tickBroadcast.getCurClockTick());
		subscribeEvent(BookOrderEvent.class,(BookOrderEvent boe)->{MessageToStartEndTimes.put(boe,new Pair(curTick,null));
																	CheckAvailability checkAvailability=new CheckAvailability(boe.getBookName());
																	Future<Integer> futureAvailability=sendEvent(checkAvailability);
																	Integer avilablity=futureAvailability.get();
																	if(avilablity!=-1) {
																		Customer customerToCharge=boe.getCustomer();
																		int price=avilablity;
																		boolean CanBeCharged=customerToCharge.canChargeCreditCard(price);
																		if(CanBeCharged) {
																			OrderResult takeOrder=Inventory.getInstance().take(boe.getBookName());
																			if(takeOrder==OrderResult.SUCCESSFULLY_TAKEN) //book is available+canBeCharged+successfully_taken
																			{
																				OrderReceipt newOrderReceipt=new OrderReceipt(boe.getBookName(),price,customerToCharge,getStartProcessTickTime(boe),getName(),boe.getEventTick(),curTick);
																				moneyRegister.file(newOrderReceipt);
																				complete(boe,newOrderReceipt);
																			}
																			else
																				complete(boe,null);
																		}
																		else {
																			complete(boe,null);
																		}
																	}
																	else
																		complete(boe,null);

																	});
	}

	public int getStartProcessTickTime(BookOrderEvent order) {
		return MessageToStartEndTimes.get(order).getKey();

	}
}