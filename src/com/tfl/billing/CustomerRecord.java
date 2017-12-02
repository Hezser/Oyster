package com.tfl.billing;

import com.oyster.OysterCard;
import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomerRecord extends CustomerDecorator {

    private final List<JourneyEvent> eventLog = new ArrayList<JourneyEvent>();

    public CustomerRecord(Customer customer) {
        super(customer);
    }

    // Convenience initializer for an OysterCard
    public CustomerRecord(String fullName, OysterCardAdapter oysterCard) {
        super(new Customer(fullName, oysterCard.getAdaptee()));
    }

    public void addEvent(JourneyEvent event) {
        eventLog.add(event);
    }

    public List<Journey> getJourneys() {
        List<Journey> journeys = new ArrayList<Journey>();
        JourneyEvent start = null;
        for (JourneyEvent event : eventLog) {
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

    public static List<CustomerRecord> getRecordsForCustumersInDatabase() {
        List<Customer> customers = CustomerDatabase.getInstance().getCustomers();
        List<CustomerRecord> customerRecords = new ArrayList<>();
        for (Customer customer : customers) {
            customerRecords.add(new CustomerRecord(customer));
        }

        return  customerRecords;
    }

}
