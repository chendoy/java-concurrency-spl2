package bgu.spl.mics.application.services;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Broadcasts.TerminateBroadcast;
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
	private int milliSecForClockTick;
	private  int duration;


	public TimeService(int milliSecForClockTick,int duration) {
		super("Time Service");
		this.milliSecForClockTick=milliSecForClockTick;
		this.duration=duration;
	}



	@Override
	protected void initialize() {
		timer=new Timer();
		currentTime=new AtomicInteger(1);

		//starting count time
		if(currentTime.get()<=duration) {
			sendBroadcast((new TickBroadcast(currentTime.get())));
		}

		timer.schedule(wrap(()-> {
					if(currentTime.incrementAndGet() <= duration) {
						sendBroadcast(new TickBroadcast(currentTime.get())); }
					else {
						sendBroadcast(new TerminateBroadcast());
						timer.cancel();
					}
				}
		),0,milliSecForClockTick);
		terminate();

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