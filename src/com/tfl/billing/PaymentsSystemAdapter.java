package com.tfl.billing;

import com.tfl.external.PaymentsSystem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PaymentsSystemAdapter implements PaymentsSystemInterface {

    private PaymentsSystem adaptee;

    private static PaymentsSystemAdapter instance = new PaymentsSystemAdapter(PaymentsSystem.getInstance());

    public static PaymentsSystemAdapter getInstance() {
        return instance;
    }

    public PaymentsSystemAdapter(PaymentsSystem paymentsSystem) {
        this.adaptee = paymentsSystem;
    }

    @Override
    public void charge(CustomerInterface customerRecord, List<JourneyInterface> journeys, BigDecimal totalBill) {
        List<Journey> convertedJourneys = new ArrayList<>();
        for (JourneyInterface journey : journeys) {
            convertedJourneys.add(new Journey(journey.getStart(), journey.getEnd()));
        }
        adaptee.charge(customerRecord.getCustomer(), convertedJourneys, totalBill);
    }
}
