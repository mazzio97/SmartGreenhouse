package iot.sgh.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class SocketClientExample implements Runnable{
	
	private Thread thread;
	private final String name;
	private Socket socket;
	
	public SocketClientExample(String name) {
		this.name = name;
	}


	@Override
	public void run(){
        String msg = "";
		while(true) {
			try {
				socket = new Socket("192.168.1.102", 9875);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				msg = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
            System.out.println(msg);
		}
	}
	
    public void start () {
    	System.out.println("Server " + name + " launched");
        if (thread == null) {
           thread = new Thread(this, name);
           thread.start ();
        }
     }
}
