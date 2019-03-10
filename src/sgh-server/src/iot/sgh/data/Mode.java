package iot.sgh.data;

import java.util.Arrays;

public enum Mode {

    AUTO("auto"), 
    MANUAL("manual");
    
    private final String msg;
    
    private Mode(String msg) {
        this.msg = msg;
    }
    
    public static Mode get(String s) {
        return Arrays.asList(Mode.values()).stream()
                                           .filter(m -> m.msg.equals(s))
                                           .findFirst()
                                           .orElseThrow(() -> new IllegalArgumentException());
    }
}
