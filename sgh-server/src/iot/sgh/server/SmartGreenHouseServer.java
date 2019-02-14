package iot.sgh.server;

import java.io.IOException;
import java.net.InetAddress;
import iot.sgh.observables.ObservableHumiditySensor;
import iot.sgh.observables.ObservableModeChange;
import iot.sgh.observables.ObservablePump;
import iot.sgh.observables.ObservableTimer;
import iot.sgh.utility.serial.SerialCommChannel;
import jssc.SerialPortList;

public class SmartGreenHouseServer {

    private static final int BOUND_RATE = 9600;

    private static SerialCommChannel channel;

    public static void main(String[] args) throws IOException {
        try {
            channel = new SerialCommChannel(SerialPortList.getPortNames()[0], BOUND_RATE);
            System.out.println("Waiting Arduino for rebooting...");
            Thread.sleep(4000);
            System.out.println("Ready.");
        } catch (Exception e) {
            System.out.println("Unplugged device, Plug and restart!");
            System.exit(-1);
        }
        
        System.out.println(InetAddress.getLocalHost());

        final ObservableHumiditySensor humiditySensor = new ObservableHumiditySensor();
        final ObservableTimer timer = new ObservableTimer();
        final ObservableModeChange modeChange = new ObservableModeChange();
        final ObservablePump pump = new ObservablePump();
        new SmartGreenHouseController(humiditySensor, timer, modeChange, pump).start();

        new SocketServerGUI(9875, "GUI").start();
        new SocketServerEdge(5050, "ESP", humiditySensor, timer).start();
        new SerialReceiver("ARDUINO", modeChange, pump).start();
        new SocketServerAndroid("ANDROID").start();
    }

    public static SerialCommChannel getChannel() {
        return channel;
    }
}
