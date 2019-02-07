package iot.sgh.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import iot.sgh.data.DataCentre;

public class SocketServerGUI extends AbstractSocketServer {
	private DataCentre data = DataCentre.getInstance();

	public SocketServerGUI(int port, String name) throws IOException {
		super(port, name);
	}

	@Override
	void job(Socket socket) throws IOException, InterruptedException {
	    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	    out.write(data.getLastPerceivedHumidity().getValue().toString());
	    out.flush();
	    socket.close();
	    Thread.sleep(1000);
	}

}
