package com.tfl.billing;

import com.oyster.OysterCardReader;
import com.oyster.ScanListener;

import java.util.Iterator;
import java.util.UUID;

public class OysterCardReaderAdapter implements CardReader {

    private OysterCardReader adaptee;

    public OysterCardReaderAdapter(OysterCardReader oysterCardReader) {
        this.adaptee = oysterCardReader;
    }

    @Override
    public void register(ScanListener scanListener) {
        this.adaptee.register(scanListener);
    }

    @Override
    public void touch(Card card) {
        if (card instanceof OysterCardAdapter) {
            touchOyster((OysterCardAdapter)card);
        }
    }

    public void touchOyster(OysterCardAdapter oysterCard) {
        this.adaptee.touch(oysterCard.getAdaptee());
    }

    @Override
    public UUID id() {
        return adaptee.id();
    }

}
