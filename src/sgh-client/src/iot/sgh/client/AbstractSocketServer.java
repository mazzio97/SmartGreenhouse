package iot.sgh.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class AbstractSocketServer extends AbstractThread {

	private static final int SLEEP_TIME = 1000;
    
	protected final ServerSocket server;
	protected Socket socket;
	
	public AbstractSocketServer(String name, int port) throws IOException {
		super(name, SLEEP_TIME);
		this.server = new ServerSocket(port);
	}

}
