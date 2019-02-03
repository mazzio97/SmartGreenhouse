package org.sgh.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class SocketServerEdge extends AbstractSocketServer {

	public SocketServerEdge(int port) throws IOException {
		super(port);
	}

	@Override
	void job(Socket socket) throws IOException {
		 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         System.out.println("ESP response: " + in.readLine());
         socket.close();
	}

}
