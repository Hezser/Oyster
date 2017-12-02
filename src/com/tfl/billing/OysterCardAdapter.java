package com.tfl.billing;

import com.oyster.OysterCard;

import java.util.UUID;

public class OysterCardAdapter implements Card {

    private OysterCard adaptee;

    public OysterCard getAdaptee() {
        return adaptee;
    }

    public OysterCardAdapter(OysterCard oysterCard) {
        this.adaptee = oysterCard;
    }

    @Override
    public UUID id() {
        return adaptee.id();
    }
}
