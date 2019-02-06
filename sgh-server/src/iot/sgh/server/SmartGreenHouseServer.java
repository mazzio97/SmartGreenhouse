package iot.sgh.server;

import java.io.IOException;
import java.net.InetAddress;

public class SmartGreenHouseServer {

	public static void main(String[] args) throws IOException {
		InetAddress inetAddress = InetAddress. getLocalHost();
		System.out.println(inetAddress.getHostAddress());
		new SocketServerEdge(9876, "ESP").start();
		new SocketServerGUI(9875, "GUI").start();
		new MessagesThread("ARDUINO").start();
	}

}
