package iot.sgh.data;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataCenter {

    public static final int MIN_HUMIDITY = 30; // percentage
    public static final int DELTA_HUMIDITY = 5; // percentage

    private static DataCenter instance = null;
    
    private final WaterPump pump = new WaterPump();
    private final Map<Instant, Float> humidity = new LinkedHashMap<>();
    private final List<Irrigation> irrigation = new LinkedList<>();
    
    private DataCenter() {
        
    }
   
    public static DataCenter getInstance() { 
	if (instance == null) 
	    instance = new DataCenter(); 
	return instance; 
    }
    
    public void recordHumidity(final float humValue) {
    	humidity.put(Instant.now(), humValue);
    }

    public void recordIrrigation() {
        
    }
    
    public Float getLastData() {
        return humidity.values().stream().collect(Collectors.toList()).get(humidity.size() - 1);
    }
    
    public Float getSecondLastData() {
        return humidity.values().stream().collect(Collectors.toList()).get(humidity.size() - 2);
    }
}
