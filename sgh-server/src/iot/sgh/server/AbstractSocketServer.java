package iot.sgh.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class AbstractSocketServer extends AbstractServerThread {
	protected final ServerSocket server;
	protected Socket socket;
		
	public AbstractSocketServer(String name, int port) throws IOException {
		super(name, 1000);
		this.server = new ServerSocket(port);
	}
	
	protected void job() throws Exception {
        socket = server.accept();
	}

}
