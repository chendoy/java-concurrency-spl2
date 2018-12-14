package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Events.AcquireVehicleEvent;
import bgu.spl.mics.application.Events.DeliveryEvent;
import bgu.spl.mics.application.Events.ReleaseVehicleEvent;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.CountDownLatch;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {
	private CountDownLatch countDownLatch;

	public LogisticsService(int i,CountDownLatch countDownLatch) {
		super("logistics "+i);
		this.countDownLatch=countDownLatch;
	}

	@Override
	protected void initialize() {
		subscribeEvent(DeliveryEvent.class,(DeliveryEvent event)-> {
																	Future<DeliveryVehicle> future = sendEvent(new AcquireVehicleEvent());
																	DeliveryVehicle acquiredVehicle = future.get();
																	acquiredVehicle.deliver(event.getCustomer().getAddress(), event.getCustomer().getDistance());
																	ReleaseVehicleEvent releaseVehicleEvent = new ReleaseVehicleEvent(acquiredVehicle);
																	sendEvent(releaseVehicleEvent);
																}
																);
		countDownLatch.countDown();
	}


}
