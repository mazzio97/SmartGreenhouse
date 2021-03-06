package iot.sgh.client;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractThread implements Runnable {

    protected Thread thread;
    protected final String name;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final int sleepTime;

    public AbstractThread(String name, int sleepTime) {
        this.name = name;
        this.sleepTime = sleepTime;
    }

    public void stop() {
        running.set(false);
    }
    
    abstract void job() throws Exception;

    public void run() {
        running.set(true);
        while (running.get()) {
            try {
                job();
                Thread.sleep(sleepTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this, name);
            thread.start();
            System.out.println(name + " launched");
        } 
    }
}
