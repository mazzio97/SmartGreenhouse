package iot.sgh.data;

import java.time.Instant;
import java.util.Map.Entry;
import java.util.Optional;

public class Irrigation {
    
    private final Entry<Instant, Double> begin;
    private Optional<Entry<Instant, Double>> end;
    private final int flow; // lt/min
    private Optional<Report> report;
    
    // package-private
    Irrigation(final int flow, final Entry<Instant, Double> begin) {
        this.flow = flow;
        this.begin = begin;
        this.end = Optional.empty();
        this.report = Optional.empty();
    }

    public boolean isFinished() {
        return this.end.isPresent();
    }
    
    public double getBeginHumidity() {
        return this.begin.getValue();
    }
    
    public Instant getBeginTime() {
        return this.begin.getKey();
    }
    
    public void end() {
        this.end = Optional.of(DataCentre.getInstance().getLastPerceivedHumidity())
                           .filter(h -> !isFinished());
    }

    public Optional<Instant> getEndTime() {
        return this.end.map(e -> e.getKey());
    }

    public int getFlow() {
        return this.flow;
    }
    
    public Optional<Long> getDuration() {
        return this.end.map(e -> e.getKey().toEpochMilli() - begin.getKey().toEpochMilli());
    }
    
    public void makeReport(Report r) {
        this.report = Optional.of(r);
    }

    public Optional<Report> getReport() {
        return this.report;
    }
    
    @Override
    public String toString() {
        return this.getBeginTime().toString() + " -> " + this.getBeginHumidity();
    }

}
