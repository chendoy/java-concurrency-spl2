package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.Events.AcquireVehicleEvent;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService implements Callback<AcquireVehicleEvent> {

	private ResourcesHolder resourcesHolder;

	public ResourceService(int i, ResourcesHolder resourcesHolder) {
		super("resources "+i);
		this.resourcesHolder=resourcesHolder;
		initialize();
	}

	@Override
	protected void initialize() {
		MessageBusImpl.getInstance().register(this);
		subscribeEvent(AcquireVehicleEvent.class,this);
	}

	@Override
	public void call(AcquireVehicleEvent c) {
		Future<DeliveryVehicle> futureVehicle=resourcesHolder.acquireVehicle();
		DeliveryVehicle vehicle=futureVehicle.get();
		complete((Event)c,vehicle);
	}
}
