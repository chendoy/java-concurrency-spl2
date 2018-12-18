package bgu.spl.mics;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A Future object represents a promised objectResult - an object that will
 * eventually be resolved to hold a objectResult of some operation. The class allows
 * Retrieving the objectResult once it is available.
 * 
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {
	private AtomicReference<T> objectResult=new AtomicReference<>();
	private boolean resolvedWithNull=false;
	
	/**
	 * This should be the the only public constructor in this class.
	 */
	public Future() {
	}
	
	/**
     * retrieves the objectResult the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     * @return return the objectResult of type T if it is available, if not wait until it is available.
     * 	       
     */
	public synchronized T get() {

		while(!isDone()&&!resolvedWithNull)
			try{this.wait();} catch (InterruptedException exp){}
		return objectResult.get();
	}
	
	/**
     * Resolves the objectResult of this Future object.
     */
	public synchronized void resolve (T result) {
		if(result==null) {
			resolvedWithNull=true;
		}
		objectResult.compareAndSet(null,result);
		notifyAll();
	}
	
	/**
     * @return true if this object has been resolved, false otherwise
     */
	public boolean isDone() {
		return !objectResult.compareAndSet(null,null);
	}
	
	/**
     * retrieves the objectResult the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     * @param unit		the {@link TimeUnit} time units to wait.
     * @return return the objectResult of type T if it is available, if not,
     * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
     *         elapsed, return null.
     */
	public T get(long timeout, TimeUnit unit) {
		long timeExpired=System.currentTimeMillis()+unit.toMillis(timeout);
		while(!isDone()) {
			long waitMs = timeExpired - System.currentTimeMillis();
			if(waitMs<=0) {
				return null;
			}
		}
		return objectResult.get();
	}

}
