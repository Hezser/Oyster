package com.tfl.billing;

import com.oyster.OysterCard;
import com.oyster.OysterCardReader;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;

import java.util.List;

public class SystemTest {

    // Expected result

    /*
    *****************


    Customer: *First Customer Information*
    Journey Summary:
                  *Time and Date*  	PADDINGTON	 *Time and Date*	BAKER_STREET
    Total charge £: 1.60


    *****************
    */

    public static void main(String[] args) throws Exception {

        List<CustomerInterface> customerRecords = CustomerRecordDatabase.getInstance().getCustomerRecords();
        String cardID = customerRecords.get(0).cardId().toString();
        OysterCard myCard = new OysterCard(cardID);
        OysterCardAdapter myCardAdapter = new OysterCardAdapter(myCard);

        OysterCardReader paddingtonReader = OysterReaderLocator.atStation(Station.PADDINGTON);
        OysterCardReaderAdapter paddingtonReaderAdapter = new OysterCardReaderAdapter(paddingtonReader);

        OysterCardReader bakerStreetReader = OysterReaderLocator.atStation(Station.BAKER_STREET);
        OysterCardReaderAdapter bakerStreetReaderAdapter = new OysterCardReaderAdapter(bakerStreetReader);

        TravelTracker travelTracker = new TravelTracker();
        travelTracker.connect(paddingtonReaderAdapter, bakerStreetReaderAdapter);
        paddingtonReaderAdapter.touch(myCardAdapter);
        minutesPass(1);
        bakerStreetReaderAdapter.touch(myCardAdapter);
        travelTracker.chargeAccounts(customerRecords, PaymentsSystemAdapter.getInstance() );
    }

    private static void minutesPass(int n) throws InterruptedException {
        Thread.sleep(n * 60 * 1000);
    }
}