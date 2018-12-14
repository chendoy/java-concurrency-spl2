package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.Broadcasts.TickBroadcast;
import bgu.spl.mics.application.Events.BookOrderEvent;
import bgu.spl.mics.application.Events.CheckAvailability;
import bgu.spl.mics.application.Events.DeliveryEvent;
import bgu.spl.mics.application.passiveObjects.*;

import java.awt.print.Book;
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
	private int startProcessTickTime;
	private ConcurrentHashMap<Message, Pair<Integer,Integer>> MessageToStartEndTimes;
	private MoneyRegister moneyRegister;
	private CountDownLatch countDownLatch;

	public SellingService(int i, MoneyRegister moneyRegister, CountDownLatch countDownLatch) {
		super("selling "+i);
		this.countDownLatch=countDownLatch;
		this.moneyRegister=moneyRegister;
	}

	@Override
	protected void initialize() {
		MessageToStartEndTimes=new ConcurrentHashMap<>();
		startProcessTickTime=-1;
		subscribeBroadcast(TickBroadcast.class,(TickBroadcast tickBroadcast)->curTick=tickBroadcast.getCurClockTick());
		subscribeEvent(BookOrderEvent.class,(BookOrderEvent boe)->{MessageToStartEndTimes.put(boe,new Pair(curTick,null));
			//System.out.println(getName()+" got bookOrderEvent of "+boe.getBookName()+" from customer "+boe.getCustomer().getName()+" and will try to sell the book");
																	CheckAvailability checkAvailability=new CheckAvailability(boe.getBookName());
																	Future<Integer> futureAvailability=sendEvent(checkAvailability);
			//System.out.println(printSellerAndCustomerNames(boe)+" send CheckAvilability Event to check if  "+boe.getBookName()+" the car exist in the store");
																	Integer avilablity=futureAvailability.get();
																	if(avilablity!=-1) {
			//System.out.println(printSellerAndCustomerNames(boe)+" got ans from InventoryService that the book "+boe.getBookName()+" is exist in the store and will try to charge now "+getCustomerName(boe));
																		Customer customerToCharge=boe.getCustomer();
																		int price=avilablity;
																		boolean CanBeCharged=customerToCharge.canChargeCreditCard(price);
																		if(CanBeCharged) {
			//System.out.println(getName()+" found that "+getCustomerName(boe)+" has enough money to puarches "+boe.getBookName()+" checks if the book in stock");
																			OrderResult takeOrder=Inventory.getInstance().take(boe.getBookName());
																			if(takeOrder==OrderResult.SUCCESSFULLY_TAKEN) //book is available+canBeCharged+successfully_taken
																			{
			//System.out.println(getName()+" got ans from Inventory that "+boe.getBookName()+" is in stock and succesfullty taken from the store. now will produce recipit and add it to the moneyRegister " );
																				OrderReceipt newOrderReceipt=new OrderReceipt(boe.getBookName(),price,customerToCharge,getStartProcessTickTime(boe),getName(),boe.getEventTick(),curTick);
																				moneyRegister.file(newOrderReceipt);

																				complete(boe,newOrderReceipt);
																			}
																			else{
			//System.out.println(getName()+" got ans from Inventory that "+boe.getBookName()+" not at stock");
																				complete(boe,null);
																			}

																		}
																		else {
		//System.out.println(getName()+" found that "+getCustomerName(boe)+" didn't have enough money to puarches "+boe.getBookName());
																			complete(boe,null);
																		}
																	}
																	else {
		//System.out.println(getName()+" got ans from InventoryService that the book "+boe.getBookName()+" doesn't exist in the store");
																		complete(boe,null);
																	}



																	});
		countDownLatch.countDown();
	}

	public int getStartProcessTickTime(BookOrderEvent order) {
		return MessageToStartEndTimes.get(order).getKey();

	}
	private String printSellerAndCustomerNames(BookOrderEvent boe){
		return getName()+" that handles "+boe.getCustomer().getName();
	}
	private String getCustomerName(BookOrderEvent boe) {
		return boe.getCustomer().getName();
	}

}