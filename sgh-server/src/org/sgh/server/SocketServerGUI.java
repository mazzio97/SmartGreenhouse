package org.sgh.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.sgh.data.DataCenter;

public class SocketServerGUI extends AbstractSocketServer {
	private DataCenter dc;

	public SocketServerGUI(int port, String name) throws IOException {
		super(port, name);
		this.dc = DataCenter.getInstance();
	}

	@Override
	void job(Socket socket) throws IOException, InterruptedException {
		 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
         out.write(dc.getLastData().toString());
         out.flush();
         socket.close();
         Thread.sleep(1000);
	}

}
