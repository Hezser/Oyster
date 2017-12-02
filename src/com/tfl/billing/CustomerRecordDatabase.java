//package com.tfl.billing;
//
//import com.tfl.external.Customer;
//import com.tfl.external.CustomerDatabase;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.UUID;
//
//public class CustomerRecordDatabase implements CustomerDatabaseInterface {
//
//    public static CustomerRecordDatabase instance = new CustomerRecordDatabase();
//
//    private CustomerRecordDatabase() {
//
//    }
//
//    public static CustomerRecordDatabase getInstance() {
//        return instance;
//    }
//
//    public List<CustomerRecord> getCustomers() {
//        List<CustomerRecord> customerRecords = new ArrayList<>();
//        List<Customer> customers = CustomerDatabase.getInstance().getCustomers();
//        for (Customer customer : customers) {
//            customerRecords.add(new CustomerRecord(customer));
//        }
//        return customerRecords;
//    }
//
//    public boolean isRegisteredId(UUID cardId) {
//        Iterator i$ = this.customers.iterator();
//
//        Customer customer;
//        do {
//            if (!i$.hasNext()) {
//                return false;
//            }
//
//            customer = (Customer)i$.next();
//        } while(!customer.cardId().equals(cardId));
//
//        return true;
//    }
//}
