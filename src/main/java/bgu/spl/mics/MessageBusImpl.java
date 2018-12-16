package bgu.spl.mics;

import bgu.spl.mics.application.services.LogisticsService;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private ConcurrentHashMap<MicroService, BlockingQueue<Message>> QueueMap;
	private ConcurrentHashMap<Class<? extends Message>, BlockingQueue<MicroService>> SubscriptionsMap ;
	private ConcurrentHashMap<Message,MicroService> messageToMicroServiceMap ;
	private ConcurrentHashMap<Message,Future> messageToFutureMap;


	private static class MessageBusImplHolder {
		private static MessageBusImpl instance =new MessageBusImpl();
	}

	private MessageBusImpl() {
		QueueMap=new ConcurrentHashMap<>();
		SubscriptionsMap=new ConcurrentHashMap<>();
		messageToMicroServiceMap=new ConcurrentHashMap<>();
		messageToMicroServiceMap=new ConcurrentHashMap<>();
		messageToFutureMap=new ConcurrentHashMap<>();
	}

	public static MessageBusImpl getInstance() {
		return MessageBusImplHolder.instance;
	}


	@Override
	public synchronized <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		BlockingQueue<MicroService>event_Subscribers=SubscriptionsMap.get(type);
		if(event_Subscribers==null) {
			event_Subscribers = new LinkedBlockingQueue<>();
			SubscriptionsMap.put(type,event_Subscribers);
		}
		event_Subscribers.add(m);
	}

	@Override
	public synchronized void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		BlockingQueue<MicroService> broadCast_Subscribers=SubscriptionsMap.get(type);

		if(broadCast_Subscribers==null) {
			broadCast_Subscribers = new LinkedBlockingQueue<>();
			SubscriptionsMap.put(type, broadCast_Subscribers);
		}
		broadCast_Subscribers.add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		MicroService microService = messageToMicroServiceMap.get(e);
		//QueueMap.get(microService).poll();
		messageToMicroServiceMap.remove(e);
		messageToFutureMap.get(e).resolve(result);
		messageToFutureMap.remove(e);
	}

	@Override
	public synchronized void sendBroadcast(Broadcast b) {
		BlockingQueue<MicroService> bSubscriptionsQueue=SubscriptionsMap.get(b.getClass());
		if(bSubscriptionsQueue!=null) {
			for (MicroService ms : bSubscriptionsQueue) {
				{
					BlockingQueue<Message> ms_Queue=QueueMap.get(ms);
					ms_Queue.add(b);

				}
			}
			notifyAll();
		}
	}
	@Override
	public synchronized <T> Future<T> sendEvent(Event<T> e) {

		BlockingQueue <MicroService> candidates_ms=SubscriptionsMap.get(e.getClass());
		if(candidates_ms!=null) {
            MicroService ms=candidates_ms.poll();
            Future<T> future=null;
            try
            {
                candidates_ms.put(ms);
                future=new Future<>();
                messageToFutureMap.put(e,future);
            }
            catch (InterruptedException ex) {}

            BlockingQueue<Message> ms_Queue=QueueMap.get(ms);
            ms_Queue.add(e);
            notifyAll();
            return future;
        }
		else {
		    return null;
        }


	}

	@Override
	public void register(MicroService m) {
		BlockingQueue<Message> newMsgQueue=new LinkedBlockingQueue<>();
		QueueMap.put(m,newMsgQueue);
	}

	//TODO: synchronised to prevent situation when the microservice gets new messages while unregistering
	@Override
	public void unregister(MicroService m) {
		if(isRegistered(m)) { //perform unregistration only if the ms was registered
			for(Message message : QueueMap.get(m)) {
				messageToMicroServiceMap.remove(message);
			}
			QueueMap.remove(m);
			Iterator it =SubscriptionsMap.entrySet().iterator();
			while (it.hasNext()) {
				ConcurrentHashMap.Entry nextPair =(ConcurrentHashMap.Entry)it.next();
				if(((BlockingQueue<MicroService>)nextPair.getValue()).contains(m))
					SubscriptionsMap.get(nextPair.getKey()).remove(m);
			}
		}
	}

	@Override
	public  synchronized Message awaitMessage(MicroService m) throws InterruptedException,IllegalStateException {
		BlockingQueue<Message> m_Queue = QueueMap.get(m);
		if(!isRegistered(m)) throw new IllegalStateException();
		while(m_Queue.isEmpty())
			this.wait();

		//if(m instanceof LogisticsService)
			//System.out.println("SIZE OF LOGISTICS QUEUE: "+m_Queue.size());

			return m_Queue.poll();


	}
	private boolean isRegistered(MicroService m) {
		return QueueMap.get(m)!=null;
	}



}
