package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.Broadcasts.TerminateBroadcast;
import bgu.spl.mics.application.Broadcasts.TickBroadcast;
import bgu.spl.mics.application.Events.BookOrderEvent;
import bgu.spl.mics.application.Events.CheckAvailability;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

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

	private int curTick=1;
	private ConcurrentHashMap<Message, Pair<Integer,Integer>> MessageToStartEndTimes;
	private MoneyRegister moneyRegister;
	private CountDownLatch countDownLatch;

	public SellingService(int i, MoneyRegister moneyRegister, CountDownLatch countDownLatch) {
		super("seller "+i);
		this.countDownLatch=countDownLatch;
		this.moneyRegister=moneyRegister;
	}

	@Override
	protected void initialize() {
		MessageToStartEndTimes=new ConcurrentHashMap<>();
		subscribeBroadcast(TickBroadcast.class,(TickBroadcast tickBroadcast)->curTick=tickBroadcast.getCurClockTick());
		subscribeEvent(BookOrderEvent.class,(BookOrderEvent boe)->{
																	MessageToStartEndTimes.put(boe,new Pair(curTick,null));
																	CheckAvailability checkAvailability=new CheckAvailability(boe.getBookName());
																	Future<Integer> futureAvailability=sendEvent(checkAvailability);
																	if(futureAvailability!=null)  // checks if InventorySrevice unregistered himself
																	{
																		Integer avilablity=futureAvailability.get();
																		if(avilablity!=null)
																		{

																		if(avilablity!=-1) {
																			Customer customerToCharge=boe.getCustomer();
																			int price=avilablity;
																			boolean CanBeCharged=customerToCharge.canChargeCreditCard(price);
																			if(CanBeCharged) {
																				OrderResult takeOrder=Inventory.getInstance().take(boe.getBookName());
																				if(takeOrder==OrderResult.SUCCESSFULLY_TAKEN) //book is available+canBeCharged+successfully_taken
																				{
																					moneyRegister.chargeCreditCard(customerToCharge,avilablity);
																					OrderReceipt newOrderReceipt=new OrderReceipt(boe.getBookName(),price,customerToCharge,getStartProcessTickTime(boe),getName(),boe.getEventTick(),curTick);
																					moneyRegister.file(newOrderReceipt);
																					customerToCharge.addOrderReceipt(newOrderReceipt);
																					complete(boe,newOrderReceipt);
																				}
																				else {
																					complete(boe,null);
																				}



																			}
																			else {
																				complete(boe,null);
																			}
																		}
																		else {
																			complete(boe,null);
																		}

																		} else {
																			complete(boe,null);
																		}

																	} else { // in this case inventoryService unregistered himself so it returned null
																		complete(boe,null);

																	}



																	});
		subscribeBroadcast(TerminateBroadcast.class,(TerminateBroadcast)->{terminate();});

		countDownLatch.countDown();
	}

	public int getStartProcessTickTime(BookOrderEvent order) {
		return MessageToStartEndTimes.get(order).getKey();

	}
}