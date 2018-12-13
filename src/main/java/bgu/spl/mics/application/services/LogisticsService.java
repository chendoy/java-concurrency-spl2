package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Events.AcquireVehicleEvent;
import bgu.spl.mics.application.Events.DeliveryEvent;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService implements Callback<DeliveryEvent> {

	public LogisticsService(int i) {
		super("logistics "+i);
		initialize();
	}

	@Override
	protected void initialize() {
		MessageBusImpl.getInstance().register(this);
		subscribeEvent(DeliveryEvent.class,this);
	}

	@Override
	public void call(DeliveryEvent c) {
		Future<AcquireVehicleEvent> future= sendEvent(new AcquireVehicleEvent());
		AcquireVehicleEvent acquiredVehicle=future.get();
		acquiredVehicle.getVehicle().deliver(c.getCustomer().getAddress(),c.getCustomer().getDistance());

	}
}
