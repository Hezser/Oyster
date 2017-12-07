package com.tfl.billing;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public interface JourneyInterface {

    UUID originId();

    UUID destinationId();

    String formattedStartTime();

    String formattedEndTime();

    Date startTime();

    Date endTime();

    JourneyEvent getStart();

    JourneyEvent getEnd();

    int durationSeconds();

    String durationMinutes();

    boolean isLong();

    boolean onPeak();

    default String format(long time) {
        return SimpleDateFormat.getInstance().format(new Date(time));
    }
}
