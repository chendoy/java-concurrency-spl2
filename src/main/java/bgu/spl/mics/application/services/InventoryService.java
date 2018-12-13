package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.Events.CheckAvailability;
import bgu.spl.mics.application.passiveObjects.Inventory;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService implements Callback<Message> {

	private Inventory inventory;

	public InventoryService(int i) {
		super("inventory "+i);
		initialize();
	}

	@Override
	protected void initialize() {
		this.inventory=Inventory.getInstance();
	}


	@Override
	public void call(Message c) {
		if(c instanceof CheckAvailability) {
			int available=inventory.checkAvailabiltyAndGetPrice(((CheckAvailability)c).getBookName());
			complete((Event)c,available);
		}
	}
}
