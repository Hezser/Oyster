package com.tfl.billing;

import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.*;

public class JourneyTest {

    // Testing variables
    UUID id1 = UUID.fromString("10000000-0000-0000-0000-00000000000");
    UUID id2 = UUID.fromString("20000000-0000-0000-0000-00000000000");
    UUID id3 = UUID.fromString("30000000-0000-0000-0000-00000000000");
    UUID id4 = UUID.fromString("40000000-0000-0000-0000-00000000000");
    Journey journey;
    String timeStart;
    String timeEnd;
    Date dateStart;
    Date dateEnd;
    Journey longJourney;
    JourneyEvent startEvent;
    JourneyEvent endEvent;

    @Before
    public void setUp() throws InterruptedException {

        // Date and time variables keep track of time in different formats, useful for testing
        long startDate = 0;
        startEvent = new JourneyStart(id1, id2, startDate);
        dateStart = new Date(0);
        dateEnd = new Date();
        dateEnd.setTime(1000 * 60 * 5);
        endEvent = new JourneyEnd(id3, id4, 1000 * 60 * 5);
        journey = new Journey(startEvent, endEvent);

        // This is a 5 minute journey from 0:00 to 0:05

    }

    @Test
    public void getsStartJourney() {
        assertTrue(journey.getStart().equals(startEvent));
    }

    @Test
    public void getsEndJourney() {
        assertTrue(journey.getEnd().equals(endEvent));
    }

    @Test
    public void returnsCorrectDurationInSeconds() {
        assertTrue(journey.durationSeconds() == (5 * 60));
    }

    @Test
    public void returnsCorrectOriginID() {
        assertTrue(journey.originId().equals(id2));
    }

    @Test
    public void returnsDestinationID() {
        assertTrue(journey.destinationId().equals(id4));
    }

    @Test
    public void returnsFormattedStartTime() {
        timeStart = SimpleDateFormat.getInstance().format(dateStart);
        assertTrue(journey.formattedStartTime().equals(timeStart));
    }

    @Test
    public void returnsFormattedEndTime() {
        timeEnd = SimpleDateFormat.getInstance().format(dateEnd);
        assertTrue(journey.formattedEndTime().equals(timeEnd));
    }

    @Test
    public void returnsStartTimeAsDate() {
        assertTrue(journey.startTime().equals(dateStart));
    }

    @Test
    public void returnsEndTimeAsDate() {
        assertTrue(journey.endTime().equals(dateEnd));
    }

    @Test
    public void returnsCorrectDurationInMinutes() throws InterruptedException {

        // Calculate minutes and seconds
        int time = journey.durationSeconds();
        int seconds = time % 60;
        int minutes = (time - seconds) / 60;

        assertTrue(journey.durationMinutes().equals("" + minutes + ":" + seconds + ""));

    }

    @Test
    public void peakJourneyIsIdentified() {

        Date peakTime = new Date();
        peakTime.setTime(1231232130); // Thu Jan 15 07:00:32 GMT 1970

        Date offPeakTime = new Date();
        offPeakTime.setTime(10); // Thu Jan 01 01:00:00 GMT 1970

        assertTrue(journey.onPeak(peakTime));
        assertFalse(journey.onPeak(offPeakTime));

    }

    @Test
    public void longJourneyIsIdentified() {

        // This is a long journey
        JourneyEvent startEvent = new JourneyStart(id1, id2);
        long time = System.currentTimeMillis() + 26 * 60 * 1000;
        JourneyEvent longEndEvent = new JourneyEnd(id3, id4, time);
        longJourney = new Journey(startEvent, longEndEvent);

        // Journey is a short journey (1 sec duration)
        assertFalse(journey.isLong());
        assertTrue(longJourney.isLong());

    }
}