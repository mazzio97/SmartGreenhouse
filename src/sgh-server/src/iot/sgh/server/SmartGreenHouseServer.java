package iot.sgh.server;

import java.io.IOException;
import java.net.InetAddress;

import iot.sgh.observables.ObservableHumiditySensor;
import iot.sgh.observables.ObservableModeChange;
import iot.sgh.observables.ObservablePump;
import iot.sgh.observables.ObservableTimer;

public class SmartGreenHouseServer { 

    public static void main(String[] args) throws IOException {
        System.out.println("Smart Green House Server running at " + InetAddress.getLocalHost().getHostAddress());

        final ObservableHumiditySensor humiditySensor = new ObservableHumiditySensor();
        final ObservableTimer timer = new ObservableTimer();
        final ObservableModeChange modeChange = new ObservableModeChange();
        final ObservablePump pump = new ObservablePump();

        new SmartGreenHouseController(humiditySensor, timer, modeChange, pump).start();
        new SocketServerGUI("ServerGUI", 4040).start();
        new SocketServerEdge("ServerESP", 5050, humiditySensor, timer).start();
        new SocketServerAndroid("ServerANDROID", 6060).start();
        new SerialReceiver("SerialARDUINO", modeChange, pump).start();
    }

}
