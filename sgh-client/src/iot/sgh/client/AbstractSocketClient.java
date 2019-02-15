package iot.sgh.client;

import java.io.IOException;
import java.net.Socket;

public abstract class AbstractSocketClient extends AbstractThread {

    private final String ip;
    private final int port;
    protected Socket socket;

    public AbstractSocketClient(String name, String ip, int port) {
        super(name, 1000);
        this.ip = ip;
        this.port = port;
    }
    
    protected void job() throws IOException {
        this.socket = new Socket(ip, port);
    }
	
}
