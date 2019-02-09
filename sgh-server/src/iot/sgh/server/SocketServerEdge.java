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
import iot.sgh.observables.ObservableHumiditySensor;
import iot.sgh.observables.ObservableTimer;

public class SocketServerEdge extends AbstractSocketServer {

    private final DataCentre data = DataCentre.getInstance();
    private final ObservableHumiditySensor humiditySensor;
    private final ObservableTimer timer;
    
    public SocketServerEdge(int port, String name, ObservableHumiditySensor humiditySensor, ObservableTimer timer) throws IOException {
        super(port, name);
        this.humiditySensor = humiditySensor;
        this.timer = timer;
    }

    @Override
    void job(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        try {
            final Instant now = Instant.now();
            final float hum = Float.parseFloat(in.readLine());
            //System.out.println(hum);
            data.recordHumidity(now, hum);
            final Optional<Irrigation> lastIrrig = data.getLastIrrigation();
            if (hum < DataCentre.MIN_HUMIDITY && lastIrrig.map(i -> i.isFinished()).orElse(true)) {
                humiditySensor.notifyEvent(new LowHumidityEvent());
                timer.scheduleTick(DataCentre.MAX_EROGATION_TIME);
            } else if (lastIrrig.filter(i -> !i.isFinished())
                                .filter(i -> hum - i.getBeginHumidity() > DataCentre.DELTA_HUMIDITY)
                                .isPresent()) {
                humiditySensor.notifyEvent(new HumidityIncreasedEvent());
                timer.stop();
            }
        } catch (NumberFormatException e) {
            System.out.println("Not a Number");
        }
        socket.close();
    }

}
