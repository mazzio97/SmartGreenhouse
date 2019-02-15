package iot.sgh.data;

import java.time.Instant;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;

public class DataCentre {

    public static final int MAX_EROGATION_TIME = 5000; // milliseconds
    public static final int MIN_HUMIDITY = 30; // percentage
    public static final int DELTA_HUMIDITY = 5; // percentage

    private static DataCentre instance = null;
    
    private Mode mode = Mode.AUTO;
    private final NavigableMap<Instant, Double> humidity = new TreeMap<>((k1, k2) -> Math.toIntExact(k1.toEpochMilli() - k2.toEpochMilli()));
    private final Deque<Irrigation> irrigation = new LinkedList<>();
    
    private DataCentre() {}
   
    public static DataCentre getInstance() { 
    	if (instance == null) 
    	    instance = new DataCentre(); 
    	return instance; 
    }
    
    public void recordHumidity(final Instant moment, final double humValue) {
    	humidity.put(moment, humValue);
    }

    public void recordIrrigation(int flow) {
        irrigation.add(new Irrigation(flow, getLastPerceivedHumidity()));
    }

    public Optional<Irrigation> getLastIrrigation() {
        return Optional.ofNullable(irrigation.peekLast());
    }
    
    public Collection<Irrigation> getExecutedIrrigations() {
        return irrigation.stream().filter(i -> i.getEndTime().isPresent()).collect(Collectors.toList());
    }
    
    public Entry<Instant, Double> getLastPerceivedHumidity() {
        return Optional.of(humidity).filter(h -> !h.isEmpty()).map(h -> h.lastEntry()).orElseThrow(() -> new IllegalStateException());
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
