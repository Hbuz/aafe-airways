package com.aafe.fareengine.service;

import com.aafe.fareengine.config.FareEngineProperties;
import com.aafe.fareengine.exception.ExternalServiceException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Service responsible for managing the registry of valid airport stations.
 * It loads the list of served stations from application properties and provides validation utilities for checking
 * if a given airport code is supported.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StationRegistryService {

    private final FareEngineProperties properties;
    private final Set<String> validStations = ConcurrentHashMap.newKeySet();

    @PostConstruct
    public void initialize() {
        refreshStations();
    }

    public void refreshStations() {
        try {
            List<String> iataCodes = properties.getServedStations();

            validStations.clear();
            validStations.addAll(iataCodes);

            log.info("Loaded {} valid airport stations", validStations.size());
        } catch (Exception e) {
            log.error("Failed to load airport stations from properties", e);
            throw new ExternalServiceException("Unable to load served stations: " + e.getMessage());
        }
    }

    public boolean isValidStation(String code) {
        return validStations.contains(code);
    }
}
