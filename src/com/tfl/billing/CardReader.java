package com.tfl.billing;

import com.oyster.ScanListener;

import java.util.UUID;

public interface CardReader {

    void register(ScanListener scanListener);

    void touch(Card card);

    UUID id();

}
