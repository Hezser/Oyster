package com.tfl.billing;

import com.oyster.OysterCard;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TravelTrackerTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    // Mock objects
    CardReader reader = context.mock(CardReader.class);
    CustomerDatabaseInterface database = context.mock(CustomerDatabaseInterface.class);
    CustomerInterface customerRecord = context.mock(CustomerInterface.class);
    JourneyInterface journey = context.mock(JourneyInterface.class);
    PaymentsSystemInterface paymentsSystem = context.mock(PaymentsSystemInterface.class);

    TravelTracker tracker = new TravelTracker();
    // Registered card with valid id of an existing customer
    List<CustomerInterface> customerRecords = CustomerRecordDatabase.getInstance().getCustomerRecords();
    String cardID = customerRecords.get(0).cardId().toString();
    OysterCardAdapter registeredCard = new OysterCardAdapter(new OysterCard(cardID));
    // Unregistered card with invalid ID
    OysterCardAdapter unregisteredCard = new OysterCardAdapter(new OysterCard("00000000-0000-0000-0000-000000000000"));

    @Test
    public void trackerIsListeningToReader() {

        context.checking(new Expectations() {{
            exactly(1).of(reader).register(tracker);
        }});

        tracker.connect(reader);

    }

    @Test(expected = UnknownOysterCardException.class)
    public void trackerIdentifiesUnregisteredCard() {
        tracker.cardScanned(unregisteredCard.id(), UUID.randomUUID());
    }

    @Test
    public void addedAndRemovedFromCurrentlyTravellingWhenTapped() {

        assertFalse(tracker.getCurrentlyTravelling().contains(registeredCard.id()));

        tracker.cardScanned(registeredCard.id(), UUID.randomUUID());
        assertTrue(tracker.getCurrentlyTravelling().contains(registeredCard.id()));

        tracker.cardScanned(registeredCard.id(), UUID.randomUUID());
        assertFalse(tracker.getCurrentlyTravelling().contains(registeredCard.id()));

    }

    private void correctAmountIsCharged(BigDecimal expectedAmount, Boolean peakJourney, Boolean longJourney, Boolean cap) {

        List<JourneyInterface> journeys = new ArrayList<>();
        journeys.add(journey);
        if (cap) {
            journeys.add(journey);
            journeys.add(journey);
            journeys.add(journey);
        }
        List<CustomerInterface> customerRecords = new ArrayList<>();
        customerRecords.add(customerRecord);

        context.checking(new Expectations() {{
            oneOf(database).getCustomerRecords(); will(returnValue(customerRecords));
        }});
        context.checking(new Expectations() {{
            atLeast(1).of(customerRecord).getJourneys(); will(returnValue(journeys));
        }});
        context.checking(new Expectations() {{
            atLeast(1).of(journey).onPeak(); will(returnValue(peakJourney));
        }});
        context.checking(new Expectations() {{
            atLeast(1).of(journey).isLong(); will(returnValue(longJourney));
        }});
        context.checking(new Expectations() {{
            oneOf(paymentsSystem).charge(customerRecord, customerRecord.getJourneys(), expectedAmount);
        }});

        tracker.chargeAccounts(database.getCustomerRecords(), paymentsSystem);

    }

    @Test
    public void peakLongJourneyIsCharged() {
        correctAmountIsCharged(tracker.PEAK_LONG_JOURNEY_PRICE.setScale(2, BigDecimal.ROUND_HALF_UP), true, true, false);
    }

    @Test
    public void peakShortJourneyIsCharged() {
        correctAmountIsCharged(tracker.PEAK_SHORT_JOURNEY_PRICE.setScale(2, BigDecimal.ROUND_HALF_UP), true, false, false);
    }

    @Test
    public void offPeakLongJourneyIsCharged() {
        correctAmountIsCharged(tracker.OFF_PEAK_LONG_JOURNEY_PRICE.setScale(2, BigDecimal.ROUND_HALF_UP), false, true, false);
    }

    @Test
    public void offPeakShortJourneyIsCharged() {
        correctAmountIsCharged(tracker.OFF_PEAK_SHORT_JOURNEY_PRICE.setScale(2, BigDecimal.ROUND_HALF_UP), false, false, false);
    }

    @Test
    public void peakCapIsApplied() {
        correctAmountIsCharged(tracker.PEAK_CAP_PRICE.setScale(2, BigDecimal.ROUND_HALF_UP), true, true, true);
    }

    @Test
    public void offPeakCapIsApplied() {
        correctAmountIsCharged(tracker.OFF_PEAK_CAP_PRICE.setScale(2, BigDecimal.ROUND_HALF_UP), false, true, true);
    }

}