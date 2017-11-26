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

import java.util.Arrays;
import java.util.List;
import org.junit.Rule;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TravelTrackerTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();
    public JUnitRuleMockery context = new JUnitRuleMockery();
    ScanListener tracker = context.mock(ScanListener.class);

    OysterCardReader reader = OysterReaderLocator.atStation(Station.PADDINGTON);

    // Registered card with valid id of an existing customer
    List<Customer> customers = CustomerDatabase.getInstance().getCustomers();
    String cardID = customers.get(0).cardId().toString();
    OysterCard registeredCard = new OysterCard(cardID);

    // Unregistered card with invalid ID
    OysterCard unregisteredCard = new OysterCard("00000000-0000-0000-0000-000000000000");

    @Test
    public void trackerIsListeningToReader() {

        context.checking(new Expectations() {{
            exactly(1).of(tracker).cardScanned(registeredCard.id(), reader.id());
        }});

        reader.register(tracker);
        reader.touch(registeredCard);
    }

    @Test
    public void trackerIdentifiesUnregisteredCard() {

        exception.expect(UnknownOysterCardException.class);

        reader.register(tracker);
        reader.touch(unregisteredCard);
    }

}