package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.Events.AcquireVehicleEvent;
import bgu.spl.mics.application.Events.DeliveryEvent;
import bgu.spl.mics.application.Events.ReleaseVehicleEvent;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.CountDownLatch;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{

	private ResourcesHolder resourcesHolder;
	private CountDownLatch countDownLatch;

	public ResourceService(int i, ResourcesHolder resourcesHolder,CountDownLatch countDownLatch) {
		super("resources "+i);
		this.resourcesHolder=resourcesHolder;
		this.countDownLatch=countDownLatch;
	}

	@Override
	protected void initialize() {
		subscribeEvent(AcquireVehicleEvent.class,(AcquireVehicleEvent event)-> { Future<DeliveryVehicle> futureVehicle = resourcesHolder.acquireVehicle();
																				DeliveryVehicle vehicle = futureVehicle.get();
																				complete(event,vehicle);});

		subscribeEvent(ReleaseVehicleEvent.class,(ReleaseVehicleEvent event)-> resourcesHolder.releaseVehicle(event.getVehicle()));
		countDownLatch.countDown();
	}


}
