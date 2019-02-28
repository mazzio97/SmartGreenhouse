package iot.sgh.data;

import java.time.Instant;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.LinkedBlockingDeque;

public class DataCentre {

    public static final int MAX_EROGATION_TIME = 5000; // milliseconds
    public static final int MIN_HUMIDITY = 30; // percentage
    public static final int DELTA_HUMIDITY = 5; // percentage
    private static DataCentre instance = null;
    
    private Mode mode = Mode.AUTO;
    private final ConcurrentNavigableMap<Instant, Double> humidityValues = new ConcurrentSkipListMap<>((k1, k2) -> Math.toIntExact(k1.toEpochMilli() - k2.toEpochMilli()));
    private final BlockingDeque<Irrigation> irrigations = new LinkedBlockingDeque<>();
    
    private DataCentre() {
        
    }
   
    public static DataCentre getInstance() { 
    	if (instance == null) 
    	    instance = new DataCentre(); 
    	return instance; 
    }
    
    public void recordHumidity(Instant moment, double humValue) {
    	humidityValues.put(moment, humValue);
    }

    public void recordIrrigation(int flow) {
        irrigations.add(new Irrigation(flow, getLastPerceivedHumidity()));
    }

    public Optional<Irrigation> getLastIrrigation() {
        return Optional.ofNullable(irrigations.peekLast());
    }
    
    public Collection<Irrigation> getExecutedIrrigations() {
        return irrigations.stream().filter(i -> i.isFinished()).collect(Collectors.toList());
    }
    
    public Entry<Instant, Double> getLastPerceivedHumidity() {
        return Optional.of(humidityValues).filter(h -> !h.isEmpty()).map(h -> h.lastEntry()).orElseThrow(() -> new IllegalStateException());
    }
    
    public void setMode(Mode m) {
        this.mode = m;
    }
    
    public void setMode(String m) {
        setMode(Mode.get(m));
    }
    
    public Mode getCurrMode() {
        return this.mode;
    }

}
