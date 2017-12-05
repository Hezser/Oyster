package com.tfl.billing;

import com.oyster.OysterCard;
import com.oyster.OysterCardReader;
import com.oyster.ScanListener;
import com.tfl.billing.Journey;
import com.tfl.billing.JourneyInterface;
import com.tfl.billing.TravelTracker;
import com.tfl.billing.UnknownOysterCardException;
import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import org.junit.Before;
import org.junit.Test;

import static org.jmock.internal.Cardinality.exactly;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TravelTrackerTest {

    @Rule public JUnitRuleMockery context = new JUnitRuleMockery();

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

    @Test
    public void paymentSystemIsNotifiedToChargeAccounts() {

        List<JourneyInterface> journeys = new ArrayList<>();
        journeys.add(journey);
        List<CustomerInterface> customerRecords = new ArrayList<>();
        customerRecords.add(customerRecord);
        context.checking(new Expectations() {{
            oneOf(database).getCustomerRecords(); will(returnValue(customerRecords));
            atLeast(1).of(customerRecord).getJourneys(); will(returnValue(journeys));
            oneOf(journey).onPeak(); will(returnValue(true));
            oneOf(journey).isLong(); will(returnValue(true));
            oneOf(paymentsSystem).charge(customerRecord, customerRecord.getJourneys(), tracker.PEAK_LONG_JOURNEY_PRICE);
        }});

        tracker.chargeAccounts(database.getCustomerRecords(), paymentsSystem);

    }

}