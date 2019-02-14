package iot.sgh.server;

import java.util.Arrays;

import iot.sgh.data.DataCentre;
import iot.sgh.data.Flow;
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
        }).addHandler(AutoModeEvent.class, (ev) -> {
            data.setMode(Mode.AUTO);
        }).addHandler(LowHumidityEvent.class, (ev) -> {
            initializeIrrigation(Flow.getWaterSupply(data.getLastPerceivedHumidity().getValue()));
        }).addHandler(HumidityIncreasedEvent.class, (ev) -> {
            terminateIrrigation();
        }).addHandler(Tick.class, (ev) -> {
            terminateIrrigation();
            data.getLastIrrigation().ifPresent(i -> {
                i.makeReport(Report.TIME_EXCEDEED);
                System.out.println(i.getReport().get().toString());
            });
        }).addHandler(ManualOpenEvent.class, (ev) -> {
            initializeIrrigation(((ManualOpenEvent) ev).getFlow());
        }).addHandler(ManualCloseEvent.class, (ev) -> {
            terminateIrrigation();
        });
    }
    
    private void initializeIrrigation(final Integer supply) {
        data.recordIrrigation(supply);
        System.out.println(supply);
        SmartGreenHouseServer.getChannel().sendMsg("sup" + supply.toString());
        System.out.println("Inizio irrigazione");
    }
    
    private void terminateIrrigation() {
        SmartGreenHouseServer.getChannel().sendMsg("sup" + "0");
        data.getLastIrrigation().get().end();
        System.out.println("Fine irrigazione");
    }
}
