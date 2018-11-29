package bgu.spl.mics;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private ConcurrentHashMap<MicroService, ConcurrentLinkedDeque<Class<? extends Message>>> QueueMap;
	private ConcurrentHashMap<Message, ConcurrentLinkedDeque<MicroService>> SubscriptionsMap ;


	private static class MessageBusImplHolder {
		private static MessageBusImpl instance =new MessageBusImpl();

	}

	private MessageBusImpl() {
		QueueMap=new ConcurrentHashMap<>();
		SubscriptionsMap=new ConcurrentHashMap<>();
	}

	public static MessageBusImpl getInstance() {
		return MessageBusImplHolder.instance;
	}


	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		SubscriptionsMap.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		SubscriptionsMap.get(type).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		ConcurrentLinkedDeque<MicroService> bSubscriptionsQueue=SubscriptionsMap.get(b);
		for(MicroService ms:bSubscriptionsQueue)
			ms.sendBroadcast(b);
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void register(MicroService m) {
		BlockingQueue<Message> mQueue=new LinkedBlockingDeque<>();
		QueueMap.put(m,mQueue);
	}

	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	

}
