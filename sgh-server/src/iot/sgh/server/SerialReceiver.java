package iot.sgh.server;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import iot.sgh.data.DataCentre;
import iot.sgh.data.Mode;
import iot.sgh.events.AutoModeEvent;
import iot.sgh.events.ManualCloseEvent;
import iot.sgh.events.ManualModeEvent;
import iot.sgh.events.ManualOpenEvent;
import iot.sgh.observables.ObservableModeChange;
import iot.sgh.observables.ObservablePump;
import iot.sgh.utility.eventloop.Observable;
import iot.sgh.utility.serial.SerialCommChannel;

public class SerialReceiver extends AbstractServerThread {

    private static final String ONLY_NUMBERS = "[^0-9]";
    private static final String ONLY_LETTERS_AND_PUNCTUATION = "[^a-zA-Z!.: ]";
    private static final String MODE_KEYWORD = "mode";
    private static final String PUMP_KEYWORD = "pump";
    
    private final Map<String, Consumer<String>> serialReceiverTasks = new HashMap<>();
    private final SerialCommChannel serial = SmartGreenHouseServer.getChannel();
    private final ObservableModeChange modeChange;
    private final ObservablePump pump;

    public SerialReceiver(String name, ObservableModeChange modeChange, ObservablePump pump) {
        super(name, 1000);
        serialReceiverTasks.put(MODE_KEYWORD, modeChangedTask());
        serialReceiverTasks.put(PUMP_KEYWORD, togglePumpTask());
        this.modeChange = modeChange;
        this.pump = pump;
    }

    @Override
    void job() throws Exception {
        if (serial.isMsgAvailable()) {
            final String msg = serial.receiveMsg();
            System.out.println(msg);
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
                modeChange.notifyEvent(new AutoModeEvent());
                break;
            case MANUAL:
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
                pump.notifyEvent(new ManualOpenEvent(flow));
            } else {
                pump.notifyEvent(new ManualCloseEvent());
            }
        };
    }
}
