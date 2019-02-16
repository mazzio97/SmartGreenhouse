package iot.sgh.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import iot.sgh.data.DataCentre;

public class SocketServerAndroid extends AbstractSocketServer {

    public SocketServerAndroid(String name, int port) throws IOException {
        super(name, port);
    }

    @Override
    protected void job() throws IOException {
        this.socket = this.server.accept();
        try {
            Integer hum = DataCentre.getInstance().getLastPerceivedHumidity().getValue().intValue();
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            out.write(hum.toString());
            out.flush();
        } catch(IllegalStateException e) {
            System.out.println(this.name + ": no humidity value available");
        }
        this.socket.close();
    }
}
