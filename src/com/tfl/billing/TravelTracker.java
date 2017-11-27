package com.tfl.billing;

import com.oyster.*;
import com.tfl.external.CustomerDatabase;
import com.tfl.external.PaymentsSystem;
import com.tfl.external.Customer;

import java.math.BigDecimal;
import java.util.*;

public class TravelTracker implements ScanListener {

    static final BigDecimal OFF_PEAK_SHORT_JOURNEY_PRICE = new BigDecimal(1.60);
    static final BigDecimal PEAK_SHORT_JOURNEY_PRICE = new BigDecimal(2.90);
    static final BigDecimal OFF_PEAK_LONG_JOURNEY_PRICE = new BigDecimal(2.70);
    static final BigDecimal PEAK_LONG_JOURNEY_PRICE = new BigDecimal(3.80);

    private final List<JourneyEvent> eventLog = new ArrayList<JourneyEvent>();

    public Set<UUID> getCurrentlyTravelling() {
        return currentlyTravelling;
    }

    private final Set<UUID> currentlyTravelling = new HashSet<UUID>();


    public void chargeAccounts() {

        List<Customer> customers = CustomerDatabase.getInstance().getCustomers();

        for (Customer customer : customers) {
            totalJourneysFor(customer);
        }
    }

    private void totalJourneysFor(Customer customer) {

        List<JourneyEvent> customerJourneyEvents = getCustomerEvents(customer);

        List<Journey> journeys = getCustomerJourneys(customerJourneyEvents);

        BigDecimal customerTotal = getCustomerTotal(journeys);

        PaymentsSystem.getInstance().charge(customer, journeys, roundToNearestPenny(customerTotal));
    }

    private List<JourneyEvent> getCustomerEvents(Customer customer) {
        List<JourneyEvent> customerJourneyEvents = new ArrayList<JourneyEvent>();

        for (JourneyEvent journeyEvent : eventLog) {
            if (journeyEvent.cardId().equals(customer.cardId())) {
                customerJourneyEvents.add(journeyEvent);
            }
        }
        return customerJourneyEvents;
    }

    private List<Journey> getCustomerJourneys(List<JourneyEvent> customerJourneyEvents) {
        List<Journey> journeys = new ArrayList<Journey>();

        JourneyEvent start = null;
        for (JourneyEvent event : customerJourneyEvents) {
            if (event instanceof JourneyStart) {
                start = event;
            }
            if (event instanceof JourneyEnd && start != null) {
                journeys.add(new Journey(start, event));
                start = null;
            }
        }
        return journeys;
    }

    private BigDecimal getCustomerTotal(List<Journey> journeys) {
        BigDecimal customerTotal = new BigDecimal(0);
        for (Journey journey : journeys) {
            BigDecimal journeyPrice;
            if (isPeak(journey)) {
                journeyPrice = isLong(journey) ? PEAK_LONG_JOURNEY_PRICE : PEAK_SHORT_JOURNEY_PRICE;
            }
            else {
                journeyPrice = isLong(journey) ? OFF_PEAK_LONG_JOURNEY_PRICE : OFF_PEAK_SHORT_JOURNEY_PRICE;
            }

            customerTotal = customerTotal.add(journeyPrice);
        }
        return customerTotal;
    }

    private BigDecimal roundToNearestPenny(BigDecimal poundsAndPence) {
        return poundsAndPence.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private boolean isPeak(Journey journey) {
        return isPeak(journey.startTime()) || isPeak(journey.endTime());
    }

    private boolean isPeak(Date time) {
        Calendar.getInstance().setTime(time);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        return (hour >= 6 && hour <= 9) || (hour >= 17 && hour <= 19);
    }

    public void connect(OysterCardReader... cardReaders) {
        for (OysterCardReader cardReader : cardReaders) {
            cardReader.register(this);
        }
    }

    private boolean isLong(Journey journey) {
        // All time is expressed in seconds
        int duration = journey.durationSeconds();
        return (duration > 25*60);
    }

    @Override
    public void cardScanned(UUID cardId, UUID readerId) {
        if (currentlyTravelling.contains(cardId)) {
            eventLog.add(new JourneyEnd(cardId, readerId));
            currentlyTravelling.remove(cardId);
        } else {
            if (CustomerDatabase.getInstance().isRegisteredId(cardId)) {
                currentlyTravelling.add(cardId);
                eventLog.add(new JourneyStart(cardId, readerId));
            } else {
                throw new UnknownOysterCardException(cardId);
            }
        }
    }

}
