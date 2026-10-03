package com.aafe.fareengine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.List;


/**
 * Configuration properties for fare calculation logic.
 * Populated from properties prefixed with 'fareengine'.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "fareengine")
public class FareEngineProperties {

    private String distanceMethod;

    private BigDecimal distanceCostPerKm;
    private BigDecimal stopoverCost;
    private BigDecimal fixedBaseCost;
    private BigDecimal taxRate;

    private List<String> servedStations;
    private List<String> forbiddenStarts;
    private List<String> forbiddenEnds;
}
