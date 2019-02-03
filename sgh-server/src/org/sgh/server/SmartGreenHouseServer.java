package org.sgh.server;

import java.io.IOException;

public class SmartGreenHouseServer {

	public static void main(String[] args) throws IOException {
		//new SocketServerEdge(9876).run();
		new SocketServerGUI(9875).run();

	}

}
