package iot.sgh.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Optional;

import iot.sgh.data.DataCentre;
import iot.sgh.data.Flow;
import iot.sgh.data.Irrigation;
import iot.sgh.data.Mode;
import iot.sgh.data.Report;
import iot.sgh.events.AutoModeEvent;
import iot.sgh.events.HumidityIncreasedEvent;
import iot.sgh.events.LowHumidityEvent;
import iot.sgh.events.ManualCloseEvent;
import iot.sgh.events.ManualModeEvent;
import iot.sgh.events.ManualOpenEvent;
import iot.sgh.events.Tick;
import iot.sgh.utility.eventloop.EventLoopControllerWithHandlers;
import iot.sgh.utility.eventloop.Observable;

public class SmartGreenHouseController extends EventLoopControllerWithHandlers {

    private static final String CLIENT_IP = "192.168.1.13";
    private static final int CLIENT_PORT = 7070;
    private static final String STATUS_TAG = "status";
    private static final String SUPPLY_TAG = "sup";
    private static final String PUMP_TAG = "pump";
    private static final String IRRIG_TAG = "irrig";
    
    private final DataCentre data = DataCentre.getInstance();

    public SmartGreenHouseController(Observable... obs) {
        Arrays.asList(obs).forEach(o -> startObserving(o));
    }
    
    @Override
    protected void setupHandlers() {
        addHandler(ManualModeEvent.class, (ev) -> {
            data.setMode(Mode.MANUAL);
            sendMsgToClient(STATUS_TAG + "manual");
        }).addHandler(AutoModeEvent.class, (ev) -> {
            data.setMode(Mode.AUTO);
            sendMsgToClient(STATUS_TAG + "auto");
        }).addHandler(LowHumidityEvent.class, (ev) -> {
            initializeIrrigation(Flow.getWaterSupply(data.getLastPerceivedHumidity().getValue()));
        }).addHandler(HumidityIncreasedEvent.class, (ev) -> {
            terminateIrrigation(Optional.empty());
        }).addHandler(Tick.class, (ev) -> {
            System.out.println("TIMER: stop irrigation (out of time)");
            terminateIrrigation(Optional.of(Report.TIME_EXCEDEED));
        }).addHandler(ManualOpenEvent.class, (ev) -> {
            initializeIrrigation(((ManualOpenEvent) ev).getFlow());
        }).addHandler(ManualCloseEvent.class, (ev) -> {
            terminateIrrigation(Optional.empty());
        });
    }
    
    private void initializeIrrigation(Integer supply) {
        data.recordIrrigation(supply);
        SerialReceiver.getChannel().sendMsg(SUPPLY_TAG + supply.toString());
        sendMsgToClient(PUMP_TAG + supply);
    }
    
    private void terminateIrrigation(Optional<Report> report) {
        SerialReceiver.getChannel().sendMsg(SUPPLY_TAG + "0");
        final Irrigation lastIrrig = data.getLastIrrigation().get();
        lastIrrig.end();
        report.ifPresent(r -> lastIrrig.makeReport(r));
        sendMsgToClient(PUMP_TAG + "0");
        sendMsgToClient(IRRIG_TAG + lastIrrig.toString());
    }
    
    public static void sendMsgToClient(String msg) {
        try {
            final Socket socket = new Socket(CLIENT_IP, CLIENT_PORT);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            out.write(msg);
            out.flush();
            socket.close();
        } catch(IOException ce) {
            System.out.println("Can't send " + msg + ": client unreachable");
        }
    }
}
