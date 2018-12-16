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
import java.util.concurrent.TimeUnit;

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
		subscribeEvent(DeliveryEvent.class,(DeliveryEvent event)-> checkForCar(event));
		countDownLatch.countDown();
	}
	private void checkForCar(DeliveryEvent event) {
		AcquireVehicleEvent ev=new AcquireVehicleEvent();
		Future<DeliveryVehicle> future = sendEvent(ev);
		//System.out.println(getName()+" got an event from webApi of "+event.getCustomer().getName()+" for new delivery now will wait for car to be avilable");
		DeliveryVehicle acquiredVehicle = future.get();
		String hii="";
		if(acquiredVehicle!=null) {
			//System.out.println(getName()+" found avilable car, now will deliver to customer "+event.getCustomer().getName());
			acquiredVehicle.deliver(event.getCustomer().getAddress(), event.getCustomer().getDistance());
			//System.out.println(getName()+" done delivery for "+event.getCustomer().getName()+" now will release the vehicle");
			sendEvent(new ReleaseVehicleEvent(acquiredVehicle));
		}
		else { //resend the event
			String hi="";
			checkForCar(event);
		}
	}


}
