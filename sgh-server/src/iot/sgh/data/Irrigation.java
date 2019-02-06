package iot.sgh.data;

import java.time.Instant;
import java.util.Optional;

public class Irrigation {
    
    public static final int MAX_DURATION = 5000; // millisecs
    
    private final Instant begin;
    private Optional<Instant> end;
    private final int flow; // lt/min
    private long duration; // millisecs
    private Optional<Report> report;
    
    public Irrigation(final int flow, final Instant begin) {
        this.flow = flow;
        this.begin = begin;
        this.report = Optional.empty();
    }
    
    public Instant getBeginTime() {
        return this.begin;
    }
    
    public int getFlow() {
        return this.flow;
    }
    
    public Optional<Long> getDuration() {
        return this.end.isPresent() ? Optional.of(duration) : Optional.empty();
    }
    
    public Optional<Report> getReport() {
        return this.report;
    }
    
    public void setEndDate(final Instant end) {
        this.end = Optional.of(end);
        this.duration = this.end.get().getEpochSecond() - this.begin.getEpochSecond();
        if (duration > MAX_DURATION) {
            makeReport(Report.TIME_EXCEDEED);
        }
    }
    
    private void makeReport(Report r) {
        this.report = Optional.of(r);
    }
}
