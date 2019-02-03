package org.sgh.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class AbstractSocketServer implements Runnable {
	private final ServerSocket server;
	
	public AbstractSocketServer(int port) throws IOException {
		this.server = new ServerSocket(port);
	}
	
	abstract void job(Socket socket) throws IOException, InterruptedException;
	
    public void run() {
        while (true) {
        	try {
        		job(server.accept());
        		System.out.println("Connection established on port: " + server.getLocalPort());
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        }
    }
}
