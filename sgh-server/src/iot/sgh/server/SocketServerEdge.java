package iot.sgh.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.time.Instant;
import java.util.Optional;

import iot.sgh.data.DataCentre;
import iot.sgh.data.Irrigation;
import iot.sgh.events.HumidityIncreasedEvent;
import iot.sgh.events.LowHumidityEvent;
import iot.sgh.events.TimeExcedeedEvent;
import iot.sgh.utility.eventloop.Observable;

public class SocketServerEdge extends AbstractSocketServer {

    private final DataCentre data = DataCentre.getInstance();
    private final Observable humiditySensor;
    
    public SocketServerEdge(int port, String name, Observable humiditySensor) throws IOException {
        super(port, name);
        this.humiditySensor = humiditySensor;
    }

    @Override
    void job(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        try {
            final Instant now = Instant.now();
            final float hum = Float.parseFloat(in.readLine());
            System.out.println(hum);
            data.recordHumidity(now, hum);
            final Optional<Irrigation> lastIrrig = data.getLastIrrigation();
            if (hum < DataCentre.MIN_HUMIDITY && lastIrrig.map(i -> i.isFinished()).orElse(true)) {
                humiditySensor.notifyEvent(new LowHumidityEvent());
            } else if (lastIrrig.filter(i -> !i.isFinished())
                                .filter(i -> hum - i.getBeginHumidity() > DataCentre.DELTA_HUMIDITY)
                                .isPresent()) {
                humiditySensor.notifyEvent(new HumidityIncreasedEvent());
            } else if (lastIrrig.filter(i -> !i.isFinished())
                                .filter(i -> now.toEpochMilli() - i.getBeginTime().toEpochMilli() > DataCentre.MAX_EROGATION_TIME)
                                .isPresent()) {                
                humiditySensor.notifyEvent(new TimeExcedeedEvent());
            }
        } catch (NumberFormatException e) {
            System.out.println("Not a Number");
        }
        socket.close();
    }

}
