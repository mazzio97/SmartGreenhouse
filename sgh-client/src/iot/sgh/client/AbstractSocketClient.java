package iot.sgh.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public abstract class AbstractSocketClient implements Runnable {
	
	private Thread thread;
	private final String name = "GUI";
	private Socket socket;
	
	abstract void job(Socket socket) throws IOException, InterruptedException;
	
    public void run() {
        while (true) {
        	try {
        		socket = new Socket("192.168.178.113", 9875);
        		job(socket);
        		Thread.sleep(1000);
        	} catch (UnknownHostException e) {
        	    System.out.println("BRUHELLA");
        	} catch (IOException e) {
        	    System.out.println("BRIOLLA");
        	} catch (InterruptedException e) {
                System.out.println("BRIELLA");
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
