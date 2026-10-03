package com.aafe.fareengine.service;

import com.aafe.fareengine.config.ServiceChargeProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServiceChargeCalculatorTest {

    private ServiceChargeCalculator calculator;

    @BeforeEach
    void setUp() {
        // Initialize service charge properties with mock data
        ServiceChargeProperties properties = new ServiceChargeProperties();

        // Charges for specific departure stations
        properties.setDeparture(Map.of(
                "SIN,AMS", BigDecimal.valueOf(50.0), // applies if the departure station is BCN or HAM
                "IAH", BigDecimal.valueOf(30.0)
        ));

        // Charges for specific arrival stations
        properties.setArrival(Map.of(
                "CMN", BigDecimal.valueOf(50.0),
                "LIM", BigDecimal.valueOf(30.0)
        ));

        // Charges for specific arrival stations
        properties.setIntermediate(Map.of(
                "BCN,HAM", BigDecimal.valueOf(35.0), // applies if the intermediate station is BCN or HAM
                "SZG", BigDecimal.valueOf(25.0)
        ));

        calculator = new ServiceChargeCalculator(properties);
    }

    @Test
    void shouldCalculateCorrectServiceCharge() {
        // Given an itinerary with served chargeable stations
        List<String> itinerary = List.of("AMS", "BCN", "HAM", "LIM");

        // When the service charge is calculated
        BigDecimal charge = calculator.calculateServiceCharge(itinerary);

        // Then the total should be:
        // Departure: AMS = 50
        // Intermediate: BCN + HAM = 35 + 35
        // Arrival: LIM = 30
        // Total = 150
        assertEquals(BigDecimal.valueOf(150.0), charge);
    }

    @Test
    void shouldReturnZeroWhenNoChargesApply() {
        // Given an itinerary with airports not contained in any of the charges map
        List<String> itinerary = List.of("XYZ", "ABC", "DEF");

        // When the service charge is calculated
        BigDecimal charge = calculator.calculateServiceCharge(itinerary);

        // Then the total should be zero
        assertEquals(BigDecimal.ZERO, charge);
    }
}
