import com.oyster.OysterCard;
import com.oyster.OysterCardReader;
import com.oyster.ScanListener;
import com.tfl.billing.TravelTracker;
import com.tfl.billing.UnknownOysterCardException;
import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
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

import java.util.List;

public class TravelTrackerTest {

    OysterCardReader reader = OysterReaderLocator.atStation(Station.PADDINGTON);

    // Registered card with valid id of an existing customer
    List<Customer> customers = CustomerDatabase.getInstance().getCustomers();
    String cardID = customers.get(0).cardId().toString();
    OysterCard registeredCard = new OysterCard(cardID);

    // Unregistered card with invalid ID
    OysterCard unregisteredCard = new OysterCard("00000000-0000-0000-0000-000000000000");

    @Test
    public void trackerIsListeningToReader() {

        TravelTracker tracker = new TravelTracker();

        context.checking(new Expectations() {{
            exactly(1).of(reader).register(tracker);
        }});

        tracker.connect(reader);
    }

    @Test
    public void journeyCorrectlyIdentifiedAsLongJourney() {

        TravelTracker tracker = new TravelTracker();

        assertTrue(tracker);

    }

    @Test(expected = UnknownOysterCardException.class)
    public void trackerIdentifiesUnregisteredCard() {

        TravelTracker tracker = new TravelTracker();

        tracker.connect(reader);
        reader.touch(unregisteredCard);
    }

    @Test
    public void addedAndRemovedFromCurrentlyTravellingWhenTapped() {

        TravelTracker tracker = new TravelTracker();

        assertFalse(tracker.getCurrentlyTravelling().contains(registeredCard.id()));

        reader.register(tracker);
        reader.touch(registeredCard);
        assertTrue(tracker.getCurrentlyTravelling().contains(registeredCard.id()));

        reader.touch(registeredCard);
        assertFalse(tracker.getCurrentlyTravelling().contains(registeredCard.id()));

    }

}