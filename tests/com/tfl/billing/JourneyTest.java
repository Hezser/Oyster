package com.tfl.billing;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class JourneyTest {

    Journey journey;

    @Before
    public void setUp() throws InterruptedException {
        JourneyEvent startEvent = new JourneyStart(UUID.fromString("00000000-0000-0000-0000-000000000000"), UUID.fromString("00000000-0000-0000-0000-000000000000"));
        Thread.sleep(1 * 1000);
        JourneyEvent endEvent = new JourneyEnd(UUID.fromString("00000000-0000-0000-0000-000000000000"), UUID.fromString("00000000-0000-0000-0000-000000000000"));
        journey = new Journey(startEvent, endEvent);
    }

    @Test
    public void returnsCorrectDurationInSeconds() throws InterruptedException {
        assertTrue(journey.durationSeconds() == 1);
    }

    @Test
    public void returnsCorrectDurationInMinutes() throws InterruptedException {
        assertTrue(journey.durationMinutes().equals("0:1"));
    }

}