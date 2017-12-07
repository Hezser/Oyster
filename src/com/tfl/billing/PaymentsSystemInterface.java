package com.tfl.billing;

import com.tfl.external.Customer;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentsSystemInterface {

    void charge(CustomerInterface customer, List<JourneyInterface> journeys, BigDecimal totalBill);

}
