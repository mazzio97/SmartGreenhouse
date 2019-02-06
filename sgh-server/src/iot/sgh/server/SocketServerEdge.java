package iot.sgh.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import iot.sgh.data.DataCenter;

public class SocketServerEdge extends AbstractSocketServer {
	private DataCenter dc;

	public SocketServerEdge(int port, String name) throws IOException {
		super(port, name);
		this.dc = DataCenter.getInstance();
	}

	@Override
	void job(Socket socket) throws IOException {
		 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		 dc.pushData(Float.parseFloat(in.readLine()));
         socket.close();
	}

}
