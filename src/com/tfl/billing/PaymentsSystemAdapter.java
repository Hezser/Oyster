package com.tfl.billing;

import com.tfl.external.PaymentsSystem;

import java.math.BigDecimal;
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
    public void charge(CustomerInterface customerRecord, List<Journey> journeys, BigDecimal totalBill) {
        adaptee.charge(customerRecord.getCustomer(), journeys, totalBill);
    }
}
