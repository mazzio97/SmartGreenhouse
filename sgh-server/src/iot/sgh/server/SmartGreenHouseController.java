package iot.sgh.server;

import iot.sgh.data.DataCentre;
import iot.sgh.data.Flow;
import iot.sgh.data.Mode;
import iot.sgh.data.Report;
import iot.sgh.events.HumidityIncreasedEvent;
import iot.sgh.events.LowHumidityEvent;
import iot.sgh.events.ModeChangedEvent;
import iot.sgh.events.Tick;
import iot.sgh.utility.eventloop.EventLoopControllerWithHandlers;
import iot.sgh.utility.eventloop.Observable;

public class SmartGreenHouseController extends EventLoopControllerWithHandlers {

    private final DataCentre data = DataCentre.getInstance();
    private SerialSender sendHumiditySerial;

    public SmartGreenHouseController(final Observable humiditySensor, final Observable timer, final Observable mode) {
        startObserving(humiditySensor);
        startObserving(timer);
        startObserving(mode);
    }
    
    @Override
    protected void setupHandlers() {
        addHandler(LowHumidityEvent.class, (ev) -> {
            final Integer supply = Flow.getWaterSupply(data.getLastPerceivedHumidity().getValue());
            data.recordIrrigation(supply);
            System.out.println(supply);
            SmartGreenHouseServer.getChannel().sendMsg("sup" + supply.toString());
            System.out.println("Inizio irrigazione");
        }).addHandler(HumidityIncreasedEvent.class, (ev) -> {
            terminateIrrigation();
        }).addHandler(Tick.class, (ev) -> {
            terminateIrrigation();
            data.getLastIrrigation().ifPresent(i -> {
                i.makeReport(Report.TIME_EXCEDEED);
                System.out.println(i.getReport().get().toString());
            });
        }).addHandler(ModeChangedEvent.class, (ev) -> {
            if (data.getCurrMode() == Mode.AUTO) {
                sendHumiditySerial.stop();
            } else {
                sendHumiditySerial = new SerialSender("SENDER");
                sendHumiditySerial.start();
            }
        });
    }
    
    private void terminateIrrigation() {
        SmartGreenHouseServer.getChannel().sendMsg("sup" + "0");
        data.getLastIrrigation().get().end();
        System.out.println("Fine irrigazione");
    }
}
