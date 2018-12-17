package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.Broadcasts.TerminateBroadcast;
import bgu.spl.mics.application.Events.CheckAvailability;
import bgu.spl.mics.application.passiveObjects.Inventory;

import java.util.concurrent.CountDownLatch;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService {
	private CountDownLatch countDownLatch;
	private Inventory inventory;

	public InventoryService(int i,CountDownLatch countDownLatch) {
		super("inventory "+i);
		this.countDownLatch=countDownLatch;
	}

	@Override
	protected void initialize() {
		this.inventory=Inventory.getInstance();
		subscribeEvent(CheckAvailability.class,(CheckAvailability checkAvailability)->{
																						int available=inventory.checkAvailabiltyAndGetPrice(checkAvailability.getBookName());
																						System.out.println("inventory service checks if" +checkAvailability.getBookName()+" exist in the store");
																						complete(checkAvailability,available);});
		subscribeBroadcast(TerminateBroadcast.class,(TerminateBroadcast)->{

			terminate();});

		countDownLatch.countDown();
	}

}
