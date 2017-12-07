package com.tfl.billing;

import com.oyster.OysterCard;
import com.oyster.OysterCardReader;
import com.oyster.ScanListener;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.api.Expectation;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;

public class OysterCardReaderAdapterTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    // Mock objects
    ScanListener listener = context.mock(ScanListener.class);

    // Instances
    OysterCardReaderAdapter oysterCardReaderAdapter;
    UUID cardID = UUID.randomUUID();
    Card card;

    @Before
    public void setUp() throws InterruptedException {
        oysterCardReaderAdapter = new OysterCardReaderAdapter(new OysterCardReader());
        card = new OysterCardAdapter(new OysterCard(cardID.toString()));
    }

    @Test
    public void touchAlertsListeners() throws Exception {

        context.checking(new Expectations() {{
            exactly(2).of(listener).cardScanned(card.id(), oysterCardReaderAdapter.id());
        }});

        oysterCardReaderAdapter.register(listener);
        oysterCardReaderAdapter.register(listener);
        oysterCardReaderAdapter.touch(card);
    }

}