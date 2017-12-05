package com.tfl.billing;

import com.oyster.OysterCard;
import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class CustomerRecordDatabase implements CustomerDatabaseInterface {

    private CustomerDatabase adaptee;

    public static CustomerRecordDatabase instance = new CustomerRecordDatabase(CustomerDatabase.getInstance());

    private List<CustomerInterface> customerRecords = new ArrayList<>() {
        {
            List<Customer> customers = CustomerDatabase.getInstance().getCustomers();
            for (Customer customer : customers) {
                this.add(new CustomerRecord(customer));
            }
        }
    };

    public List<CustomerInterface> getCustomerRecords() {
        return customerRecords;
    }

    private CustomerRecordDatabase(CustomerDatabase customerDatabase) {
        this.adaptee = customerDatabase;
    }

    public static CustomerRecordDatabase getInstance() {
        return instance;
    }

    public boolean isRegisteredId(UUID cardId) {
        return adaptee.isRegisteredId(cardId);
    }
}
