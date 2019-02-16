package iot.sgh.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import iot.sgh.data.Mode;
import iot.sgh.events.AutoModeEvent;
import iot.sgh.events.ManualCloseEvent;
import iot.sgh.events.ManualModeEvent;
import iot.sgh.events.ManualOpenEvent;
import iot.sgh.observables.ObservableModeChange;
import iot.sgh.observables.ObservablePump;
import iot.sgh.utility.serial.SerialCommChannel;
import jssc.SerialPortList;

public class SerialReceiver extends AbstractThread {

    private static final int BOUND_RATE = 9600;
    private static final String ONLY_NUMBERS = "[^0-9]";
    private static final String ONLY_LETTERS_AND_PUNCTUATION = "[^a-zA-Z!.: ]";
    private static final String MODE_KEYWORD = "mode";
    private static final String PUMP_KEYWORD = "pump";
    private static SerialCommChannel channel;

    private final Map<String, Consumer<String>> serialReceiverTasks = new HashMap<>();
    private final ObservableModeChange modeChange;
    private final ObservablePump pump;

    public SerialReceiver(String name, ObservableModeChange modeChange, ObservablePump pump) {
        super(name, 1000);
        try {
            channel = new SerialCommChannel(SerialPortList.getPortNames()[0], BOUND_RATE);
            System.out.println("Waiting Arduino for rebooting...");
            Thread.sleep(4000);
            System.out.println("Ready.");
        } catch (Exception e) {
            System.out.println("Unplugged device, Plug and restart!");
            System.exit(-1);
        }
        serialReceiverTasks.put(MODE_KEYWORD, modeChangedTask());
        serialReceiverTasks.put(PUMP_KEYWORD, togglePumpTask());
        this.modeChange = modeChange;
        this.pump = pump;
    }

    public static SerialCommChannel getChannel() {
        return channel;
    }
    
    @Override
    void job() throws IOException, InterruptedException {
        if (channel.isMsgAvailable()) {
            final String msg = channel.receiveMsg();
            serialReceiverTasks.entrySet().stream()
                                          .filter(e -> msg.contains(e.getKey()))
                                          .findAny()
                                          .ifPresent(e -> e.getValue().accept(msg.replaceFirst(e.getKey(), "")));
        }
    }

    private final Consumer<String> modeChangedTask() {
        return (msg) -> {
            switch (Mode.get(msg.replaceAll(ONLY_LETTERS_AND_PUNCTUATION, ""))) {
            case AUTO:
                System.out.println(name + ": passing to mode AUTO");
                modeChange.notifyEvent(new AutoModeEvent());
                break;
            case MANUAL:
                System.out.println(name + ": passing to mode MANUAL");
                modeChange.notifyEvent(new ManualModeEvent());
                break;
            default:
                break;
            }
        };
    }
    
    private final Consumer<String> togglePumpTask() {
        return (msg) -> {
            int flow = Integer.parseInt(msg.replaceAll(ONLY_NUMBERS, ""));
            if (flow > 0) {
                System.out.println(name + ": start irrigation (manual)...");
                pump.notifyEvent(new ManualOpenEvent(flow));
            } else {
                System.out.println(name + ": stop irrigation (manual)");
                pump.notifyEvent(new ManualCloseEvent());
            }
        };
    }
}
