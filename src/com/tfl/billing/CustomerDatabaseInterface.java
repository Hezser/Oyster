package com.tfl.billing;

import java.util.List;
import java.util.UUID;

public interface CustomerDatabaseInterface {

    List<CustomerInterface> getCustomerRecords();

    boolean isRegisteredId(UUID cardId);

}
