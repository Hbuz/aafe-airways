package com.aafe.fareengine.service;

import com.aafe.fareengine.config.FareEngineProperties;
import com.aafe.fareengine.dto.airport.AirportLocationResponse;
import com.aafe.fareengine.dto.airport.AirportResponse;
import com.aafe.fareengine.exception.AirportNotFoundException;
import com.aafe.fareengine.exception.UnknownDistanceStrategyException;
import com.aafe.fareengine.mapper.AirportMapper;
import com.aafe.fareengine.mapper.LatitudeMapper;
import com.aafe.fareengine.mapper.LongitudeMapper;
import com.aafe.fareengine.model.Itinerary;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static nl.altindag.log.LogCaptor.forClass;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class FareCalculationServiceTest {
    @Mock
    private AirportClient airportClient;
    @Spy
    LatitudeMapper latitudeMapper = Mappers.getMapper(LatitudeMapper.class);
    @Spy
    LongitudeMapper longitudeMapper = Mappers.getMapper(LongitudeMapper.class);
    @Spy
    @InjectMocks
    AirportMapper airportMapper = Mappers.getMapper(AirportMapper.class);
    @Mock
    private DistanceCalculator distanceCalculator;
    @Mock
    private FareEngineProperties fareEngineProperties;
    @Mock
    private ServiceChargeCalculator serviceChargeCalculator;
    @InjectMocks
    private FareCalculationService fareCalculationService;

    @BeforeEach
    void setUp() {
        // Default distance method used in all tests unless overridden
        when(fareEngineProperties.getDistanceMethod()).thenReturn("haversine");
    }

    @Test
    void shouldThrowExceptionWhenAirportNotFound() {
        String unservedAirport = "ICN";

        // Simulate airport client throwing exception for unknown airport
        when(airportClient.getAirportDetails(unservedAirport)).thenThrow(new AirportNotFoundException(unservedAirport));

        AirportNotFoundException exception = assertThrows(AirportNotFoundException.class, () ->
                fareCalculationService.calculateFare(List.of(unservedAirport, "BCN")));

        assertTrue(exception.getMessage().contains("Airport not found"));
    }

    @Test
    void shouldCalculateFareCorrectlyForSimpleItinerary() {
        String airportDepart = "AMS";
        String airportArrival = "BCN";
        List<String> airports = List.of(airportDepart, airportArrival);

        // Dummy airport responses with minimal location data
        AirportResponse dummyResponse1 = new AirportResponse();
        dummyResponse1.setLocation(new AirportLocationResponse());
        AirportResponse dummyResponse2 = new AirportResponse();
        dummyResponse2.setLocation(new AirportLocationResponse());

        when(airportClient.getAirportDetails(airportDepart)).thenReturn(dummyResponse1);
        when(airportClient.getAirportDetails(airportArrival)).thenReturn(dummyResponse2);
        when(distanceCalculator.calculate(eq("haversine"), any(), any())).thenReturn(1.0);

        when(serviceChargeCalculator.calculateServiceCharge(airports)).thenReturn(BigDecimal.valueOf(1));

        when(fareEngineProperties.getDistanceCostPerKm()).thenReturn(BigDecimal.valueOf(0.50));
        when(fareEngineProperties.getStopoverCost()).thenReturn(BigDecimal.valueOf(30.0));
        when(fareEngineProperties.getFixedBaseCost()).thenReturn(BigDecimal.valueOf(40.0));
        when(fareEngineProperties.getTaxRate()).thenReturn(BigDecimal.valueOf(0.20));

        Itinerary result = fareCalculationService.calculateFare(airports);

        verify(airportClient).getAirportDetails(airportDepart);
        verify(airportClient).getAirportDetails(airportArrival);

        assertEquals(2, result.getAirports().size());
        assertTrue(result.getDistance() > 0);
        assertEquals(1, result.getDistance());

        // Fare = (1.0 km * 0.50) + 40.00 base + 1.00 service charge = 41.50
        // Tax = 20% of 41.50 = 8.30 --> Total = 49.80
        assertEquals(0, result.getFare().compareTo(BigDecimal.valueOf(49.80)));
        assertEquals(0, result.getTax().compareTo(BigDecimal.valueOf(8.30)));
    }

    @Test
    void shouldThrowsExceptionForUnsupportedDistanceMethod() {
        String unsupportedStrategy = "unsupported";

        // Simulate an unsupported distance strategy
        when(fareEngineProperties.getDistanceMethod()).thenReturn(unsupportedStrategy);
        when(distanceCalculator.calculate(eq(unsupportedStrategy), any(), any()))
                .thenThrow(new UnknownDistanceStrategyException(unsupportedStrategy));

        AirportResponse dummyResponse = new AirportResponse();
        dummyResponse.setLocation(new AirportLocationResponse());
        when(airportClient.getAirportDetails(anyString())).thenReturn(dummyResponse);

        assertThrows(UnknownDistanceStrategyException.class, () ->
                fareCalculationService.calculateFare(List.of("AMS", "BCN")));
    }

    @ParameterizedTest
    @MethodSource("itineraryProvider")
    void shouldCalculateFareForVariousItineraries(List<String> airports, double distance, BigDecimal serviceCharge, BigDecimal expectedFare) {
        // Dummy airport response for all airports
        AirportResponse dummyResponse = new AirportResponse();
        dummyResponse.setLocation(new AirportLocationResponse());

        when(airportClient.getAirportDetails(anyString())).thenReturn(dummyResponse);
        when(distanceCalculator.calculate(any(), any(), any())).thenReturn(distance);
        when(serviceChargeCalculator.calculateServiceCharge(airports)).thenReturn(serviceCharge);

        when(fareEngineProperties.getDistanceCostPerKm()).thenReturn(BigDecimal.valueOf(0.50));
        when(fareEngineProperties.getStopoverCost()).thenReturn(BigDecimal.valueOf(30.0));
        when(fareEngineProperties.getFixedBaseCost()).thenReturn(BigDecimal.valueOf(40.0));
        when(fareEngineProperties.getTaxRate()).thenReturn(BigDecimal.valueOf(0.20));

        Itinerary result = fareCalculationService.calculateFare(airports);

        // Assert that the calculated fare matches the expected total for each itinerary
        assertEquals(airports.size(), result.getAirports().size());
        assertEquals(0, result.getFare().compareTo(expectedFare));
    }

    static Stream<Arguments> itineraryProvider() {
        // Arguments of itinerary, distance (km), service charge, expected total fare (with tax)
        return Stream.of(
                Arguments.of(List.of("AMS", "BCN"), 200.0, BigDecimal.valueOf(1), BigDecimal.valueOf(169.20)),
                Arguments.of(List.of("AMS", "BCN", "LAX"), 1000.0, BigDecimal.valueOf(10), BigDecimal.valueOf(1296.00)),
                Arguments.of(List.of("AMS", "LAX"), 750.0, BigDecimal.ZERO, BigDecimal.valueOf(498.00))
        );
    }

    @Test
    void shouldLogFareBreakdown() {
        LogCaptor logCaptor = forClass(FareCalculationService.class);
        logCaptor.setLogLevelToInfo();

        List<String> airports = List.of("AMS", "BCN", "LAX");

        AirportResponse dummyResponse = new AirportResponse();
        dummyResponse.setLocation(new AirportLocationResponse());

        when(airportClient.getAirportDetails(anyString())).thenReturn(dummyResponse);
        when(distanceCalculator.calculate(any(), any(), any())).thenReturn(1000.0);
        when(serviceChargeCalculator.calculateServiceCharge(airports)).thenReturn(BigDecimal.valueOf(1));

        when(fareEngineProperties.getDistanceCostPerKm()).thenReturn(BigDecimal.valueOf(0.50));
        when(fareEngineProperties.getStopoverCost()).thenReturn(BigDecimal.valueOf(30.0));
        when(fareEngineProperties.getFixedBaseCost()).thenReturn(BigDecimal.valueOf(40.0));
        when(fareEngineProperties.getTaxRate()).thenReturn(BigDecimal.valueOf(0.20));

        fareCalculationService.calculateFare(airports);

        // Verify that the fare breakdown is logged at INFO level
        List<String> logs = logCaptor.getInfoLogs();
        assertTrue(logs.stream().anyMatch(log -> log.contains("Fare breakdown")));
    }
}
