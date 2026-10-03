package com.aafe.fareengine.exception;

public class UnknownDistanceStrategyException extends RuntimeException {
    public UnknownDistanceStrategyException(String strategyName) {
        super("Unknown distance calculation strategy: " + strategyName);
    }
}
