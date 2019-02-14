package iot.sgh.server;

import iot.sgh.data.DataCentre;

public class SerialSender extends AbstractServerThread {
    private final static String HUMIDITY_TAG = "hum";
    public SerialSender(String name) {
        super(name, 1000);
    }

    @Override
    void job() throws Exception {
        //System.out.println(HUMIDITY_TAG + String.valueOf(DataCentre.getInstance().getLastPerceivedHumidity().getValue()));
        // SmartGreenHouseServer.getChannel().sendMsg(HUMIDITY_TAG + String.valueOf(DataCentre.getInstance().getLastPerceivedHumidity().getValue()));
    }
}
