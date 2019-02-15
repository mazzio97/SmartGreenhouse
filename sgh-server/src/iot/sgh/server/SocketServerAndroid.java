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
    protected void job() throws Exception {
        super.job();
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        Integer humidity = DataCentre.getInstance().getLastPerceivedHumidity().getValue().intValue();
        out.write(humidity.toString());
        out.flush();
        socket.close();
    }
}
