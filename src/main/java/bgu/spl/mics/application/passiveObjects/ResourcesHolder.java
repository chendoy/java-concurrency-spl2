package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder implements Serializable {

	private BlockingQueue<DeliveryVehicle> vehiclesResource; //free vehicles
	private BlockingQueue<Future<DeliveryVehicle>> futureVehicles; //waiting vehicles

	private static class SingletonHolder{
		private static ResourcesHolder instance=new ResourcesHolder();
	}

	private ResourcesHolder(){
		vehiclesResource=new LinkedBlockingDeque<>();
		futureVehicles=new LinkedBlockingQueue<>();
	}
	
	/**
     * Retrieves the single instance of this class.
     */
	public static ResourcesHolder getInstance() {
		return SingletonHolder.instance;
	}
	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {

		Future<DeliveryVehicle> future=new Future<>();
		synchronized (vehiclesResource) {
			if(!vehiclesResource.isEmpty()) {
				DeliveryVehicle vehicle=vehiclesResource.poll();
				future.resolve(vehicle);
			}
			else
				futureVehicles.add(future);
		}
		return future;
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		synchronized (vehiclesResource) {
			if(!futureVehicles.isEmpty()) {
				//get the future vehicles which waited the most
				Future<DeliveryVehicle> futureVehicle=futureVehicles.poll();
				futureVehicle.resolve(vehicle);
			}
			else
				vehiclesResource.add(vehicle);
		}
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		vehiclesResource.clear();
		futureVehicles.clear();
		for (DeliveryVehicle v:vehicles) {
			vehiclesResource.add(v);
		}
	}

}
