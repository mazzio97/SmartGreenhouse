package iot.sgh.server;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import iot.sgh.data.DataCentre;
import iot.sgh.utility.serial.SerialCommChannel;

public class SerialReceiver extends AbstractServerThread {

    // private static final String ONLY_NUMBERS = "[^0-9]";
    private static final String ONLY_LETTERS_AND_PUNCTUATION = "[^a-zA-Z!.: ]";
    private static final String MODE_KEYWORD = "mode";
    
    private final Map<String, Consumer<String>> serialReceiverTasks = new HashMap<>();
    private final SerialCommChannel serial = SmartGreenHouseServer.getChannel();

    public SerialReceiver(String name) {
        super(name, 1000);
        serialReceiverTasks.put(MODE_KEYWORD, modeChangedTask());
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
            DataCentre.getInstance().setMode(msg.replaceAll(ONLY_LETTERS_AND_PUNCTUATION, ""));
        };
    }
}
