package iot.sgh.server;

import iot.sgh.data.DataCenter;
import iot.sgh.utility.SerialCommChannel;
import jssc.SerialPortList;

public class MessagesThread implements Runnable {
    private static final int SLEEP_TIME = 1000;
    private static final int BOUND_RATE = 9600;
    private SerialCommChannel channel;
    private final String name;
    private Thread thread;
    
    public MessagesThread(String name) {
    	this.name = name;
	}
    
    @Override
    public void run() {
        while(true) {
            try {
                if (DataCenter.getInstance().getLastData() >= 60 && DataCenter.getInstance().getSecondLastData() < 60) {
                	channel.sendMsg("100"); // Intensity of LED                    
                }
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this, name);
            try {
            	channel = new SerialCommChannel(SerialPortList.getPortNames()[0], BOUND_RATE);
            	System.out.println("Waiting Arduino for rebooting...");
            	Thread.sleep(4000);
            	System.out.println("Ready.");
            } catch (Exception e) {
            	System.out.println("Unplugged device, Plug and restart!");
            	System.exit(-1);
            }
            thread.start();
        }
    }
    
}
