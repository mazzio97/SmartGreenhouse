package iot.sgh.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import iot.sgh.data.DataCentre;

public class SocketServerAndroid extends AbstractSocketServer {
    public SocketServerAndroid(int port, String name) throws IOException {
        super(name, port);
    }

    @Override
    protected void job() throws IOException {
        super.job();
        try {
            Integer humidity = DataCentre.getInstance().getLastPerceivedHumidity().getValue().intValue();
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            out.write(humidity.toString());
            out.flush();
        } catch(IllegalStateException e) {
            System.out.println(this.name + ": " + "no humidity value available");
        }
        socket.close();
    }
}
