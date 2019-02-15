package iot.sgh.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class AbstractSocketServer extends AbstractThread {
	protected final ServerSocket server;
	protected Socket socket;
		
	public AbstractSocketServer(String name, int port) throws IOException {
		super(name, 1000);
		this.server = new ServerSocket(port);
	}
	
	protected void job() throws IOException {
        socket = server.accept();
	}

}
