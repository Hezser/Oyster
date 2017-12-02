package com.tfl.billing;

import com.tfl.external.Customer;

import java.util.UUID;

public abstract class CustomerDecorator implements CustomerInterface {

    private Customer customer;

    public CustomerDecorator(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }

    @Override
    public UUID cardId() {
        return this.customer.cardId();
    }

    @Override
    public String fullName() {
        return this.customer.fullName();
    }

}
