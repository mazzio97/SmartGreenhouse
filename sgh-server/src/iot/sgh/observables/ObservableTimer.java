package iot.sgh.observables;

import java.util.concurrent.*;

import iot.sgh.events.Tick;
import iot.sgh.utility.eventloop.*;

public class ObservableTimer extends Observable {

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private ScheduledFuture<?> tickHandle;
	private Runnable tickTask;
	
	public ObservableTimer() {
	    tickTask = () -> {
	    	Tick ev = new Tick(System.currentTimeMillis());
	    	notifyEvent(ev);
	    };
	}
	
	/**
	 * Start generating tick event
	 * 
	 * @param period period in milliseconds
	 */
	public synchronized void start(long period) {
		tickHandle = scheduler.scheduleAtFixedRate(tickTask, 0, period, TimeUnit.MILLISECONDS);	    
	}

	/**
	 * Generate a tick event after a number of milliseconds
	 * 
	 * @param delta
	 */
	public synchronized void scheduleTick(long deltat) {
	    tickHandle = scheduler.schedule(tickTask, deltat, TimeUnit.MILLISECONDS);
	}

	/**
	 * Stop generating tick event
	 * 
	 * @param period period in milliseconds
	 */
	public synchronized void stop() {
		if (tickHandle != null){
			tickHandle.cancel(false);
			tickHandle = null;
		}
	}

}
