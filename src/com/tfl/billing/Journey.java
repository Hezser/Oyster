package com.tfl.billing;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Journey implements JourneyInterface {

    private final JourneyEvent start;
    private final JourneyEvent end;

    public Journey(JourneyEvent start, JourneyEvent end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public UUID originId() {
        return start.readerId();
    }

    @Override
    public UUID destinationId() {
        return end.readerId();
    }

    @Override
    public String formattedStartTime() {
        return format(start.time());
    }

    @Override
    public String formattedEndTime() {
        return format(end.time());
    }

    @Override
    public Date startTime() {
        return new Date(start.time());
    }

    @Override
    public Date endTime() {
        return new Date(end.time());
    }

    @Override
    public int durationSeconds() {
        return (int) ((end.time() - start.time()) / 1000);
    }

    @Override
    public boolean onPeak() {
        return onPeak(startTime()) || onPeak(endTime());
    }

    public boolean onPeak(Date time) {
        Calendar.getInstance().setTime(time);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        return (hour >= 6 && hour <= 9) || (hour >= 17 && hour <= 19);
    }

    @Override
    public boolean isLong() {
        // All time is expressed in seconds
        int duration = durationSeconds();
        return (duration > 25*60);
    }

    @Override
    public String durationMinutes() {
        return "" + durationSeconds() / 60 + ":" + durationSeconds() % 60;
    }

}
