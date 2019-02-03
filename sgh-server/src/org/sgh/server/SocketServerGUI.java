package org.sgh.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SocketServerGUI extends AbstractSocketServer {

	public SocketServerGUI(int port) throws IOException {
		super(port);
	}

	@Override
	void job(Socket socket) throws IOException, InterruptedException {
		 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
         out.write("Hello from Java!\n");
         out.flush();
         socket.close();
         Thread.sleep(1000);
	}

}
