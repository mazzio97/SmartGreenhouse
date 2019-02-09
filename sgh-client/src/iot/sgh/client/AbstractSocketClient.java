package iot.sgh.client;

import java.io.IOException;
import java.net.Socket;

public abstract class AbstractSocketClient implements Runnable {
	
	private Thread thread;
	private final String name = "GUI";
	private Socket socket;
	
	abstract void job(Socket socket) throws IOException, InterruptedException;
	
    public void run() {
        while (true) {
        	try {
        		socket = new Socket("192.168.1.102", 9875);
        		job(socket);
        		Thread.sleep(1000);
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        }
    }
    
    public void start () {
    	System.out.println("Client " + name + " launched");
        if (thread == null) {
           thread = new Thread(this, name);
           thread.start ();
        }
     }
}
