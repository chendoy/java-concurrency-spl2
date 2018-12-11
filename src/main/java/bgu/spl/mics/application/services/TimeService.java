package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Broadcasts.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcasts}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private AtomicInteger currentTime;
	private Timer timer;


	public TimeService(int milliSecForClockTick,int duration) {
		super("Time Service");
		initialize();
		run(milliSecForClockTick,duration);
	}


	//not sure 100%
	private void run(int milliSecForClockTick,int duration) {
		//starting count time
		if(currentTime.get()<=duration) {
			MessageBusImpl.getInstance().sendBroadcast((new TickBroadcast(currentTime.get())));
		}

		timer.schedule(wrap(()-> {
					if(currentTime.incrementAndGet() <= duration) {
						MessageBusImpl.getInstance().sendBroadcast(new TickBroadcast(currentTime.get())); }
					else {
						timer.cancel();
					}
				}
		),0,milliSecForClockTick);



	}



//		int time=currentTime.get();
//		while (time<=duration) {
//			Broadcasts curTimeBroadCast=new TickBroadcast(time);
//			MessageBusImpl.getInstance().sendBroadcast(curTimeBroadCast);
//		}



	@Override
	protected void initialize() {
		timer=new Timer();
		currentTime=new AtomicInteger(1);
	}

	private static TimerTask wrap(Runnable r) {
		return new TimerTask() {

			@Override
			public void run() {
				r.run();
			}
		};
	}
}