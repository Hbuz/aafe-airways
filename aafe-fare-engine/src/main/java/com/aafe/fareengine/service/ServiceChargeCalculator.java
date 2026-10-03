package com.aafe.fareengine.service;

import com.aafe.fareengine.config.ServiceChargeProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service responsible for calculating service charges for an itinerary based on departure, arrival, and intermediate
 * airport charges.
 */
@Slf4j
@Service
public class ServiceChargeCalculator {

    private final Map<String, BigDecimal> departureCharges;
    private final Map<String, BigDecimal> arrivalCharges;
    private final Map<String, BigDecimal> intermediateCharges;

    public ServiceChargeCalculator(ServiceChargeProperties properties) {
        this.departureCharges = flattenChargeMap(properties.getDeparture());
        this.arrivalCharges = flattenChargeMap(properties.getArrival());
        this.intermediateCharges = flattenChargeMap(properties.getIntermediate());
    }

    public BigDecimal calculateServiceCharge(List<String> airports) {
        BigDecimal total = BigDecimal.ZERO;

        String departure = airports.get(0);
        String arrival = airports.get(airports.size() - 1);
        List<String> intermediates = airports.subList(1, airports.size() - 1);

        // Calculate and log departure and arrival charges
        BigDecimal departureCharge = getCharge(departureCharges, departure);
        BigDecimal arrivalCharge = getCharge(arrivalCharges, arrival);
        logCharge("departure", departure, departureCharge);
        logCharge("arrival", arrival, arrivalCharge);

        total = total.add(departureCharge).add(arrivalCharge);

        // Calculate and log intermediate charges
        for (String stop : intermediates) {
            BigDecimal stopCharge = getCharge(intermediateCharges, stop);
            logCharge("intermediate", stop, stopCharge);
            total = total.add(stopCharge);
        }

        log.debug("Total service charge for itinerary {}: €{}", airports, total);
        return total;
    }


    /**
     * Retrieves the charge for a given airport from the specified charge map.
     * Returns zero if the airport is not listed.
     */
    private BigDecimal getCharge(Map<String, BigDecimal> chargeMap, String airport) {
        return chargeMap.getOrDefault(airport, BigDecimal.ZERO);
    }


    /**
     * Converts a map of comma-separated airport codes to a flat map of individual codes.
     */
    private Map<String, BigDecimal> flattenChargeMap(Map<String, BigDecimal> original) {
        return original.entrySet().stream()
                .flatMap(entry -> Arrays.stream(entry.getKey().split(","))
                        .map(code -> Map.entry(code.trim(), entry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void logCharge(String type, String airport, BigDecimal charge) {
        log.debug("Service charge for {} airport {}: €{}", type, airport, charge);
    }
}
