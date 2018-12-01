package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private ResourceService resourceService;
	private MoneyRegister moneyRegister;
	private Inventory inventory;

	private static class TimeServiceHolder{
		private static TimeService instance =new TimeService();
	}

	public static TimeService getInstance() {
		return TimeService.TimeServiceHolder.instance;
	}


	private TimeService() {
		super("Change_This_Name");
		// TODO Implement this
	}

	//to implement this
	public int getCurrentTick() {
		return -5;
	}

	@Override
	protected void initialize() {
		// TODO Implement this
		
	}

}
