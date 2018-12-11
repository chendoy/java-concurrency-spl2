package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Broadcasts.TickBroadcast;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService implements Callback<TickBroadcast> {

	private int curBookId;
	private int startProcessTickTime;

	public SellingService(int i) {
		super("selling "+i);
	}

	@Override
	protected void initialize() {
		startProcessTickTime=-1;
		MessageBusImpl.getInstance().register(this);
		MessageBusImpl.getInstance().subscribeBroadcast(TickBroadcast.class,this);


		// TODO Implement this

	}

	public int getStartProcessTickTime() {
		return startProcessTickTime;
	}

	@Override
	public void call(TickBroadcast c) {
	if(startProcessTickTime==-1)
		startProcessTickTime=c.getCurClockTick();
	}
}