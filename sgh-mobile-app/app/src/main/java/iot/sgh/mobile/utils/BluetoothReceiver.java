package iot.sgh.mobile.utils;


import android.widget.ProgressBar;

public class BluetoothReceiver extends AbstractServerThread {
    private final ProgressBar humidityValue;

    public BluetoothReceiver(String name, ProgressBar humidityValue) {
        super(name);
        this.humidityValue = humidityValue;
    }

    @Override
    void job() {
    }

}
