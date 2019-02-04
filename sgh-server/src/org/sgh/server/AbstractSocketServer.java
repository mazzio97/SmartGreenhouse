package org.sgh.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class AbstractSocketServer implements Runnable {
	
	private Thread thread;
	private final String name;
	private final ServerSocket server;
	
	public AbstractSocketServer(int port, String name) throws IOException {
		this.server = new ServerSocket(port);
		this.name = name;
	}
	
	abstract void job(Socket socket) throws IOException, InterruptedException;
	
    public void run() {
        while (true) {
        	try {
        		job(server.accept());
        		Thread.sleep(1000);
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        }
    }
    
    public void start () {
    	System.out.println("Server " + name + " launched");
        if (thread == null) {
           thread = new Thread(this, name);
           thread.start ();
        }
     }
}
