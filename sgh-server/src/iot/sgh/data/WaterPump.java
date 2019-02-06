package iot.sgh.data;

import java.util.Arrays;

public class WaterPump {
    private boolean open;
    
    // package private
    WaterPump() {
        
    }
    
    public void open() {
        this.open = true;
    }
    
    public void close() {
        this.open = false;
    }
    
    public boolean isOpen() {
        return this.open;
    }
    
    public int getWaterSupply(final float humidity) {
        return Arrays.asList(Flow.values()).stream()
                                           .filter(f -> f.minHumidity <= humidity && f.maxHumidity > humidity)
                                           .findFirst()
                                           .map(f -> f.waterSupply)
                                           .orElse(0);
    }
    
    public enum Flow {
        MIN(DataCenter.MIN_HUMIDITY * 2/3, DataCenter.MIN_HUMIDITY, 80), 
        MED(DataCenter.MIN_HUMIDITY * 1/3, DataCenter.MIN_HUMIDITY * 2/3, 160), 
        MAX(0, DataCenter.MIN_HUMIDITY * 1/3, 240);
        
        private final float minHumidity;
        private final float maxHumidity;
        private final int waterSupply; // lt/min
        
        private Flow(final float minHumidity, final float maxHumidity, int waterSupply) {
            this.minHumidity = minHumidity;
            this.maxHumidity = maxHumidity;
            this.waterSupply = waterSupply;
        }

    }
}
