package com.aafe.fareengine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for service charges applied to itineraries.
 * Populated from properties prefixed with 'fareengine.service-charges'.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "fareengine.service-charges")
public class ServiceChargeProperties {
    private Map<String, BigDecimal> departure = new HashMap<>();
    private Map<String, BigDecimal> arrival = new HashMap<>();
    private Map<String, BigDecimal> intermediate = new HashMap<>();
}
