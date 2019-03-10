package iot.sgh.data;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map.Entry;
import java.util.Optional;

public class Irrigation {
    
    private final Entry<Instant, Double> begin;
    private Optional<Entry<Instant, Double>> end;
    private final int flow; // lt/min
    private Optional<Report> report;
    
    // package-private
    Irrigation(int flow, Entry<Instant, Double> begin) {
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
    
    public Optional<Double> getHumidityDifference() {
        return this.end.map(e -> e.getValue() - begin.getValue());        
    }
    
    public void makeReport(Report r) {
        this.report = Optional.of(r);
    }

    public Optional<Report> getReport() {
        return this.report;
    }
    
    @Override
    public String toString() {
        return "Date: " + getDateFromInstant(this.getBeginTime()) + "\n" +
               "Period: " + getTimeFromInstant(this.getBeginTime()) + " -> " + this.getEndTime().map(t -> getTimeFromInstant(t)).orElse("") + "\n" +
               "Flow: " + this.getFlow() + " lt/min\n" + 
               "Hum. Difference: " + getHumidityDifference().map(d -> d.toString() + "%").orElse("UNDEFINED") + "\n" +
               getReport().map(r -> "Report: " + r.getMessage()).orElse("");
    }
    
    private final String getTimeFromInstant(Instant i) {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(i, ZoneId.systemDefault());
        return String.format("%02d", zdt.getHour()) + ":" + 
               String.format("%02d", zdt.getMinute()) + ":" + 
               String.format("%02d", zdt.getSecond());
    }
    
    private final String getDateFromInstant(Instant i) {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(i, ZoneId.systemDefault());
        return String.format("%02d", zdt.getDayOfMonth()) + "/" + 
               String.format("%02d", zdt.getMonthValue()) + "/" + 
               String.valueOf(zdt.getYear());
    }

}
