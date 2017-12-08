package com.tfl.billing;

import com.tfl.external.PaymentsSystem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PaymentsSystemAdapter implements PaymentsSystemInterface {

    private static PaymentsSystemAdapter instance = new PaymentsSystemAdapter();

    public static PaymentsSystemAdapter getInstance() {
        return instance;
    }

    private PaymentsSystemAdapter() {
    }

    @Override
    public void charge(CustomerInterface customerRecord, List<JourneyInterface> journeys, BigDecimal totalBill) {
        List<Journey> convertedJourneys = new ArrayList<>();
        for (JourneyInterface journey : journeys) {
            convertedJourneys.add(new Journey(journey.getStart(), journey.getEnd()));
        }
        PaymentsSystem.getInstance().charge(customerRecord.getCustomer(), convertedJourneys, totalBill);
    }
}
