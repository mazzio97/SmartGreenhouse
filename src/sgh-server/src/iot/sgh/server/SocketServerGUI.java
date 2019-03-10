package iot.sgh.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import iot.sgh.data.DataCentre;

public class SocketServerGUI extends AbstractSocketServer {

	private final DataCentre data = DataCentre.getInstance();

	public SocketServerGUI(String name, int port) throws IOException {
		super(name, port);
	}

	@Override
	protected void job() throws IOException {
	    this.socket = this.server.accept();
	    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
	    ZonedDateTime zdt = ZonedDateTime.ofInstant(data.getLastPerceivedHumidity().getKey(), ZoneId.systemDefault());
	    out.write(data.getLastPerceivedHumidity().getValue() + ":" + zdt.getSecond());
	    out.flush();
	    this.socket.close();
	}
}
