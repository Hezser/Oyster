package com.tfl.billing;

import com.oyster.OysterCard;
import com.tfl.external.Customer;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class CustomerRecordTest {

    CustomerRecord customerRecord;
    OysterCardAdapter oysterCard;

    @Before
    public void setUp() {
        oysterCard = new OysterCardAdapter(new OysterCard(UUID.randomUUID().toString()));
        customerRecord = new CustomerRecord(new Customer("Robert Chatley", oysterCard.getAdaptee()));
    }

    @Test
    public void journeysAreCorrectlyExtracted() throws Exception {

        JourneyEvent start = new JourneyStart(oysterCard.id(), UUID.randomUUID());
        JourneyEvent end = new JourneyEnd(oysterCard.id(), UUID.randomUUID());
        Journey expectedJourney = new Journey(start, end);

        assertTrue(customerRecord.getJourneys().equals(new ArrayList<>()));

        customerRecord.addEvent(start);
        customerRecord.addEvent(end);

        // Array has only one journey, and that journey is the same as the expected journey (same start and end times)
        assertTrue(customerRecord.getJourneys().size() == 1);
        assertTrue(customerRecord.getJourneys().get(0).startTime().equals(expectedJourney.startTime()));
        assertTrue(customerRecord.getJourneys().get(0).endTime().equals(expectedJourney.endTime()));

    }

}