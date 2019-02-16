package iot.sgh.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.Optional;

import iot.sgh.data.DataCentre;
import iot.sgh.data.Irrigation;
import iot.sgh.data.Mode;
import iot.sgh.events.HumidityIncreasedEvent;
import iot.sgh.events.LowHumidityEvent;
import iot.sgh.observables.ObservableHumiditySensor;
import iot.sgh.observables.ObservableTimer;

public class SocketServerEdge extends AbstractSocketServer {

    private final DataCentre data = DataCentre.getInstance();
    private final ObservableHumiditySensor humiditySensor;
    private final ObservableTimer timer;

    public SocketServerEdge(String name, int port, 
                            ObservableHumiditySensor humiditySensor, ObservableTimer timer) throws IOException {
        super(name, port);
        this.humiditySensor = humiditySensor;
        this.timer = timer;
    }

    @Override
    protected void job() throws IOException {
        this.socket = this.server.accept();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            float hum = Float.parseFloat(in.readLine());
            data.recordHumidity(Instant.now(), hum);
            if (data.getCurrMode().equals(Mode.AUTO)) {
                Optional<Irrigation> lastIrrig = data.getLastIrrigation();
                if (hum < DataCentre.MIN_HUMIDITY && lastIrrig.map(i -> i.isFinished()).orElse(true)) {
                    System.out.println(name + ": start irrigation...");
                    humiditySensor.notifyEvent(new LowHumidityEvent());
                    timer.scheduleTick(DataCentre.MAX_EROGATION_TIME);
                } else if (lastIrrig.filter(i -> !i.isFinished())
                                    .filter(i -> hum - i.getBeginHumidity() > DataCentre.DELTA_HUMIDITY).isPresent()) {
                    System.out.println(name + ": stop irrigation");
                    humiditySensor.notifyEvent(new HumidityIncreasedEvent());
                    timer.stop();
                }
            }
        } catch (NumberFormatException e) {
            System.out.println(name + ": incorrect humidity value");
        }
        this.socket.close();
    }

}
