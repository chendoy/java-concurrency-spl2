package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Broadcasts.TickBroadcast;
import bgu.spl.mics.application.Events.OrderBookEvent;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.Pair;

import java.security.MessageDigest;
import java.util.concurrent.ConcurrentHashMap;

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
public class SellingService extends MicroService implements Callback<Message> {

	private int curBookId;
	private int curTick=1;
	private int startProcessTickTime;
	private ConcurrentHashMap<Message, Pair<Integer,Integer>> MessageToStartEndTimes;
	private MoneyRegister moneyRegister;

	public SellingService(int i) {
		super("selling "+i);
	}

	@Override
	protected void initialize() {
		MessageToStartEndTimes=new ConcurrentHashMap<>();
		startProcessTickTime=-1;
		MessageBusImpl.getInstance().register(this);
		MessageBusImpl.getInstance().subscribeBroadcast(TickBroadcast.class,this);
		MessageBusImpl.getInstance().subscribeEvent(OrderBookEvent.class,this);
	}

	@Override
	public void call(Message c) {
		if(c instanceof OrderBookEvent) {
			MessageToStartEndTimes.put(c,new Pair(curTick,null));
		}
		else if (c instanceof TickBroadcast) {
			curTick=((TickBroadcast) c).getCurClockTick();
		}
	}
	public int getStartProcessTickTime(OrderBookEvent order) {
		return MessageToStartEndTimes.get(order).getKey();

	}
}