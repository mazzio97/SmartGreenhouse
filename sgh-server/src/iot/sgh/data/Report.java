package iot.sgh.data;

public enum Report {
    TIME_EXCEDEED("Maximum time for irrigation reached");
    
    private String message;
    
    private Report(String msg) {
        this.message = msg;
    }
    
    public String getMessage() {
        return this.message;
    }
}
