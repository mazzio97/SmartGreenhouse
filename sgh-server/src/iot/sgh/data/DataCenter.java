package iot.sgh.data;

import java.util.ArrayList;
import java.util.List;

public class DataCenter {
	
    private static DataCenter instance = null; 
    private List<Float> humidity;
    
    private DataCenter() {
		humidity = new ArrayList<>();
    }
   
    public static DataCenter getInstance() { 
	    if (instance == null) 
	        instance = new DataCenter(); 
	    return instance; 
    } 
    
    public void pushData(Float h) {
    	humidity.add(h);
    }
    
    public Float getLastData() {
    	return humidity.get(humidity.size() - 1);
    }
    
    public Float getSecondLastData() {
    	return humidity.get(humidity.size() - 2);
    }
}
