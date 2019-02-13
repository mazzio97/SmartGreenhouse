package iot.sgh.mobile.utils;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractServerThread implements Runnable {
    private static final int SLEEP_TIME = 1000;
    protected Thread thread;
    private final String name;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public AbstractServerThread(String name) {
        this.name = name;
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
                Thread.sleep(SLEEP_TIME);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this, name);
            thread.start();
            System.out.println("Server " + name + " launched");
        }
    }
}
