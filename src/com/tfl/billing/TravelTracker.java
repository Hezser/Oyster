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
    static final BigDecimal PEAK_CAP_PRICE = new BigDecimal(9.00);
    static final BigDecimal OFF_PEAK_CAP_PRICE = new BigDecimal(7.00);

    public Set<UUID> getCurrentlyTravelling() {
        return currentlyTravelling;
    }

    private final Set<UUID> currentlyTravelling = new HashSet<UUID>();

    public void chargeAccounts(List<CustomerInterface> customerRecords, PaymentsSystemInterface paymentSystem) {

        for (CustomerInterface customerRecord : customerRecords) {
            List<JourneyInterface> journeys = customerRecord.getJourneys();
            BigDecimal customerTotal = getCustomerTotal(journeys);
            paymentSystem.charge(customerRecord, journeys, roundToNearestPenny(customerTotal));
        }
    }

    private BigDecimal getCustomerTotal(List<JourneyInterface> journeys) {
        Boolean travelledOnPeak = false;
        BigDecimal customerTotal = new BigDecimal(0);
        for (JourneyInterface journey : journeys) {
            BigDecimal journeyPrice;
            if (journey.onPeak()) {
                journeyPrice = journey.isLong() ? PEAK_LONG_JOURNEY_PRICE : PEAK_SHORT_JOURNEY_PRICE;
                travelledOnPeak = true;
            }
            else {
                journeyPrice = journey.isLong() ? OFF_PEAK_LONG_JOURNEY_PRICE : OFF_PEAK_SHORT_JOURNEY_PRICE;
            }

            customerTotal = customerTotal.add(journeyPrice);
        }

        if (travelledOnPeak && (customerTotal.compareTo(PEAK_CAP_PRICE) == 1)) {    // Meaning that customerTotal > PEAK_CAP_PRICE
            return PEAK_CAP_PRICE;
        }
        else if (!travelledOnPeak && (customerTotal.compareTo(OFF_PEAK_CAP_PRICE) == 1)) {
            return OFF_PEAK_CAP_PRICE;
        }
        return customerTotal;
    }

    private BigDecimal roundToNearestPenny(BigDecimal poundsAndPence) {
        return poundsAndPence.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public void connect(CardReader... cardReaders) {
        for (CardReader cardReader : cardReaders) {
            cardReader.register(this);
        }
    }

    private CustomerInterface customerForCardId(UUID cardId) throws UnknownOysterCardException {
        List<CustomerInterface> customerRecords = CustomerRecordDatabase.getInstance().getCustomerRecords();
        for (CustomerInterface customerRecord : customerRecords) {
            if (customerRecord.cardId().equals(cardId)) {
                return customerRecord;
            }
        }

        throw new UnknownOysterCardException(cardId);
    }

    @Override
    public void cardScanned(UUID cardId, UUID readerId) {
        if (currentlyTravelling.contains(cardId)) {
            customerForCardId(cardId).addEvent(new JourneyEnd(cardId, readerId));
            currentlyTravelling.remove(cardId);
        } else {
            if (CustomerRecordDatabase.getInstance().isRegisteredId(cardId)) {
                currentlyTravelling.add(cardId);
                customerForCardId(cardId).addEvent(new JourneyStart(cardId, readerId));
            } else {
                throw new UnknownOysterCardException(cardId);
            }
        }
    }

}
