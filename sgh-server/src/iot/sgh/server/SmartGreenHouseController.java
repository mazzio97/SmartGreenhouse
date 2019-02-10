package iot.sgh.server;

import iot.sgh.data.DataCentre;
import iot.sgh.data.Flow;
import iot.sgh.data.Report;
import iot.sgh.events.HumidityIncreasedEvent;
import iot.sgh.events.LowHumidityEvent;
import iot.sgh.events.ModeChangedEvent;
import iot.sgh.events.Tick;
import iot.sgh.utility.eventloop.EventLoopControllerWithHandlers;
import iot.sgh.utility.eventloop.Observable;

public class SmartGreenHouseController extends EventLoopControllerWithHandlers {

    private final DataCentre data = DataCentre.getInstance();
    
    public SmartGreenHouseController(final Observable humiditySensor, final Observable timer) {
        startObserving(humiditySensor);
        startObserving(timer);
    }
    
    @Override
    protected void setupHandlers() {
        addHandler(LowHumidityEvent.class, (ev) -> {
            final Integer supply = Flow.getWaterSupply(data.getLastPerceivedHumidity().getValue());
            data.recordIrrigation(supply);
            SmartGreenHouseServer.getChannel().sendMsg(supply.toString());                                        
            System.out.println("Inizio irrigazione");
        }).addHandler(HumidityIncreasedEvent.class, (ev) -> {
            terminateIrrigation();
        }).addHandler(Tick.class, (ev) -> {
            terminateIrrigation();
            data.getLastIrrigation().ifPresent(i -> {
                i.makeReport(Report.TIME_EXCEDEED);
                System.out.println(i.getReport().get().toString());
            });
        });
    }
    
    private void terminateIrrigation() {
        SmartGreenHouseServer.getChannel().sendMsg("0");
        data.getLastIrrigation().get().end();
        System.out.println("Fine irrigazione");
    }
}
