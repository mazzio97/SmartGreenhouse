package iot.sgh.events;

import iot.sgh.utility.eventloop.Event;

public class ManualOpenEvent implements Event {
    
    private final int flow;
    
    public ManualOpenEvent(final int flow) {
        this.flow = flow;
    }

    public int getFlow() {
        return this.flow;
    }
}
