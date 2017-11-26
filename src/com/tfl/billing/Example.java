package com.tfl.billing;

import com.oyster.OysterCard;
import com.oyster.OysterCardReader;
import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;

import java.util.List;

public class Example {
    public static void main(String[] args) throws Exception {
        String cardID;
        List<Customer> customers = CustomerDatabase.getInstance().getCustomers();
        cardID = customers.get(0).cardId().toString();
        OysterCard myCard = new OysterCard("00000000-0000-0000-0000-000000000000");
        OysterCardReader paddingtonReader = OysterReaderLocator.atStation(Station.PADDINGTON);
        OysterCardReader bakerStreetReader = OysterReaderLocator.atStation(Station.BAKER_STREET);
        OysterCardReader kingsCrossReader = OysterReaderLocator.atStation(Station.KINGS_CROSS);
        TravelTracker travelTracker = new TravelTracker();
        travelTracker.connect(paddingtonReader, bakerStreetReader, kingsCrossReader);
        paddingtonReader.touch(myCard);
        minutesPass(1);
        bakerStreetReader.touch(myCard);
        minutesPass(1);
        bakerStreetReader.touch(myCard);
        minutesPass(1);
        kingsCrossReader.touch(myCard);
        travelTracker.chargeAccounts();
    }
    private static void minutesPass(int n) throws InterruptedException {
        Thread.sleep(n * 60 * 1000);
    }
}