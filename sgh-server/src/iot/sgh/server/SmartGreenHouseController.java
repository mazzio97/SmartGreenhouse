package iot.sgh.server;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
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

    private final DataCentre data = DataCentre.getInstance();

    public SmartGreenHouseController(final Observable... obs) {
        Arrays.asList(obs).forEach(o -> startObserving(o));
    }
    
    @Override
    protected void setupHandlers() {
        addHandler(ManualModeEvent.class, (ev) -> {
            data.setMode(Mode.MANUAL);
            sendMsgToClient("status" + "manual");
        }).addHandler(AutoModeEvent.class, (ev) -> {
            data.setMode(Mode.AUTO);
            sendMsgToClient("status" + "auto");
        }).addHandler(LowHumidityEvent.class, (ev) -> {
            initializeIrrigation(Flow.getWaterSupply(data.getLastPerceivedHumidity().getValue()));
        }).addHandler(HumidityIncreasedEvent.class, (ev) -> {
            terminateIrrigation(Optional.empty());
        }).addHandler(Tick.class, (ev) -> {
            terminateIrrigation(Optional.of(Report.TIME_EXCEDEED));
        }).addHandler(ManualOpenEvent.class, (ev) -> {
            initializeIrrigation(((ManualOpenEvent) ev).getFlow());
        }).addHandler(ManualCloseEvent.class, (ev) -> {
            terminateIrrigation(Optional.empty());
        });
    }
    
    private void initializeIrrigation(final Integer supply) {
        data.recordIrrigation(supply);
        SmartGreenHouseServer.getChannel().sendMsg("sup" + supply.toString());
        System.out.println("Inizio irrigazione");
        sendMsgToClient("pump" + supply);
    }
    
    private void terminateIrrigation(Optional<Report> report) {
        SmartGreenHouseServer.getChannel().sendMsg("sup" + "0");
        final Irrigation lastIrrig = data.getLastIrrigation().get();
        lastIrrig.end();
        report.ifPresent(r -> lastIrrig.makeReport(r));
        System.out.println("Fine irrigazione");
        sendMsgToClient("pump" + "0");
        sendMsgToClient("irrig" + lastIrrig.toString());
    }
    
    public static void sendMsgToClient(String msg) {
        try {
            final Socket socket = new Socket("192.168.1.13", 7070);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            out.write(msg);
            out.flush();
            socket.close();
        } catch(ConnectException ce) {
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
