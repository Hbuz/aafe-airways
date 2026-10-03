package com.aafe.fareengine.exception;

public class AirportNotFoundException extends RuntimeException {
    public AirportNotFoundException(String airportCode) {
        super("Airport not found: " + airportCode);
    }
}
