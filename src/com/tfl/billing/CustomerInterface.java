package com.tfl.billing;

import com.tfl.external.Customer;

import java.util.List;
import java.util.UUID;

public interface CustomerInterface {

    UUID cardId();

    String fullName();

    Customer getCustomer();

    List<JourneyInterface> getJourneys();

    void addEvent(JourneyEvent event);

}
