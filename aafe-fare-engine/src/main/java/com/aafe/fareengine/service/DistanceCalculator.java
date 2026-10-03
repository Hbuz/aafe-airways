package com.aafe.fareengine.service;

import com.aafe.fareengine.exception.UnknownDistanceStrategyException;
import com.aafe.fareengine.model.Coordinates;
import com.aafe.fareengine.service.distance.DistanceStrategy;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * Service responsible for calculating distances between two coordinates using a pluggable strategy pattern.
 * Strategies are injected via Spring and selected by name at runtime.
 */
@Slf4j
@Service
public class DistanceCalculator {
    private final Map<String, DistanceStrategy> strategies;

    public DistanceCalculator(Map<String, DistanceStrategy> strategies) {
        this.strategies = Map.copyOf(strategies);   // to prevent accidental changes to the map at runtime
    }

    @PostConstruct
    public void validateStrategies() {
        if (strategies == null || strategies.isEmpty()) {
            throw new UnknownDistanceStrategyException("No distance strategies configured.");
        }
    }

    public double calculate(String strategyName, Coordinates from, Coordinates to) {
        DistanceStrategy strategy = strategies.get(strategyName);
        if (strategy == null) {
            throw new UnknownDistanceStrategyException(strategyName);
        }

        log.debug("Using distance strategy '{}' for coordinates: from={} to={}", strategyName, from, to);
        return strategy.calculate(from, to);
    }
}
