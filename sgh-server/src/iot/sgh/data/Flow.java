package iot.sgh.data;

import java.util.Arrays;

public enum Flow {
    MIN(DataCentre.MIN_HUMIDITY * 2/3, DataCentre.MIN_HUMIDITY, 25), 
    MED(DataCentre.MIN_HUMIDITY * 1/3, DataCentre.MIN_HUMIDITY * 2/3, 100), 
    MAX(0, DataCentre.MIN_HUMIDITY * 1/3, 250);
    
    private final float minHumidity;
    private final float maxHumidity;
    private final int waterSupply; // lt/min
    
    private Flow(final float minHumidity, final float maxHumidity, int waterSupply) {
        this.minHumidity = minHumidity;
        this.maxHumidity = maxHumidity;
        this.waterSupply = waterSupply;
    }

    public static int getWaterSupply(final double humidity) {
        return Arrays.asList(Flow.values()).stream()
                                           .filter(f -> f.minHumidity <= humidity && f.maxHumidity > humidity)
                                           .findFirst()
                                           .map(f -> f.waterSupply)
                                           .orElse(0);
    }
    
}
