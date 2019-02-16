package iot.sgh.data;

public enum Report {

    TIME_EXCEDEED("Time Excedeed");
    
    private String message;
    
    private Report(String msg) {
        this.message = msg;
    }
    
    public String getMessage() {
        return this.message;
    }
}
