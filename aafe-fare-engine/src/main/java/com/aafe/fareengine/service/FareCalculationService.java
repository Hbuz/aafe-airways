package com.aafe.fareengine.service;

import com.aafe.fareengine.config.FareEngineProperties;
import com.aafe.fareengine.dto.airport.AirportResponse;
import com.aafe.fareengine.exception.AirportNotFoundException;
import com.aafe.fareengine.mapper.AirportMapper;
import com.aafe.fareengine.model.Coordinates;
import com.aafe.fareengine.model.FareBreakdown;
import com.aafe.fareengine.model.Itinerary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.IntStream;


/**
 * Service responsible for calculating the total fare for a given itinerary.
 * It combines distance-based costs, stopover fees, fixed base costs, service charges, and tax.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FareCalculationService {

    private final AirportClient airportClient;
    private final AirportMapper airportMapper;
    private final DistanceCalculator distanceCalculator;
    private final ServiceChargeCalculator serviceChargeCalculator;
    private final FareEngineProperties properties;

    public Itinerary calculateFare(List<String> airports) {
        Integer totalDistance = getTotalDistance(airports);
        FareBreakdown breakdown = getFareBreakdown(airports, totalDistance);

        return Itinerary.builder()
                .airports(airports)
                .fare(breakdown.totalFare())
                .tax(breakdown.tax())
                .distance(totalDistance)
                .build();
    }

    private FareBreakdown getFareBreakdown(List<String> airports, int totalDistance) {
        int intermediateStops = airports.size() - 2;
        BigDecimal distanceCost = properties.getDistanceCostPerKm().multiply(BigDecimal.valueOf(totalDistance));
        BigDecimal stopoverCost = properties.getStopoverCost().multiply(BigDecimal.valueOf(intermediateStops));
        BigDecimal fixedBaseCost = properties.getFixedBaseCost();
        BigDecimal serviceCharge = serviceChargeCalculator.calculateServiceCharge(airports);

        log.debug("Distance cost: €{}", distanceCost);
        log.debug("Stopover cost ({} stops): €{}", intermediateStops, stopoverCost);
        log.debug("Fixed base cost: €{}", fixedBaseCost);
        log.debug("Service charge: €{}", serviceCharge);

        BigDecimal baseFare = distanceCost.add(stopoverCost).add(fixedBaseCost).add(serviceCharge);
        BigDecimal tax = baseFare.multiply(properties.getTaxRate());
        BigDecimal totalFare = baseFare.add(tax);

        log.debug("Base fare: €{}", baseFare);
        log.debug("Tax ({}%): €{}", properties.getTaxRate().multiply(BigDecimal.valueOf(100)), tax);
        log.debug("Total fare: €{}", totalFare);

        FareBreakdown breakdown = new FareBreakdown(
                round(baseFare),
                round(tax),
                round(totalFare),
                round(distanceCost),
                round(stopoverCost),
                round(fixedBaseCost),
                round(serviceCharge)
        );
        logFareBreakdown(airports, totalDistance, breakdown);
        return breakdown;
    }

    private void logFareBreakdown(List<String> airports, int distance, FareBreakdown breakdown) {
        if (log.isInfoEnabled()) {
            log.info("""
                            Fare breakdown for itinerary {} ({} km):
                                - Distance cost: €{}
                                - Stopover cost: €{}
                                - Fixed base cost: €{}
                                - Service Charge: €{}
                                - Base fare: €{}
                                - Tax (21%): €{}
                                - Total fare: €{}""",
                    airports, distance,
                    breakdown.distanceCost().toPlainString(),
                    breakdown.stopoverCost().toPlainString(),
                    breakdown.fixedBaseCost().toPlainString(),
                    breakdown.serviceCharge().toPlainString(),
                    breakdown.baseFare().toPlainString(),
                    breakdown.tax().toPlainString(),
                    breakdown.totalFare().toPlainString());
        }
    }

    private Integer getTotalDistance(List<String> airports) {
        String strategyName = properties.getDistanceMethod();

        double totalDistance = IntStream.range(0, airports.size() - 1)
                .mapToDouble(i -> {
                    String fromCode = airports.get(i);
                    String toCode = airports.get(i + 1);
                    log.debug("Calculating distance from {} to {}", fromCode, toCode);

                    Coordinates from = getCoordinates(fromCode);
                    Coordinates to = getCoordinates(toCode);
                    double legDistance = distanceCalculator.calculate(strategyName, from, to);
                    log.debug("Distance between {} and {}: {} km", fromCode, toCode, legDistance);

                    return legDistance;
                })
                .sum();

        return (int) totalDistance;
    }

    private Coordinates getCoordinates(String airportCode) {
        log.debug("Fetching coordinates for airport: {}", airportCode);

        AirportResponse response = airportClient.getAirportDetails(airportCode);

        if (response == null || response.getLocation() == null) {
            throw new AirportNotFoundException(airportCode);
        }

        Coordinates coordinates = airportMapper.locationDtoToCoordinates(response.getLocation());
        log.debug("Mapped coordinates for {}: {}", airportCode, coordinates);

        return coordinates;
    }

    private BigDecimal round(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
