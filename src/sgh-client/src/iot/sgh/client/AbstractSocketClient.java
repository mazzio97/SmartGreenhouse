package iot.sgh.client;

import java.net.Socket;

public abstract class AbstractSocketClient extends AbstractThread {

    private static final int SLEEP_TIME = 1000;

    protected final String ip;
    protected final int port;
    protected Socket socket;

    public AbstractSocketClient(String name, String ip, int port) {
        super(name, SLEEP_TIME);
        this.ip = ip;
        this.port = port;
    }
	
}
