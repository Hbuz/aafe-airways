package com.aafe.fareengine.service;

import com.aafe.fareengine.config.FareEngineProperties;
import com.aafe.fareengine.exception.ExternalServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StationRegistryServiceTest {

    @Mock
    private FareEngineProperties fareEngineProperties;

    @InjectMocks
    private StationRegistryService stationRegistryService;

    @Test
    void shouldLoadStationsAndValidateCorrectly() {
        // Given a list of served airport codes
        mockAirports("AMS", "LAX", "CDG");

        // Then the service should validate served airport codes as valid
        assertTrue(stationRegistryService.isValidStation("AMS"));
        assertTrue(stationRegistryService.isValidStation("LAX"));

        // And not served airport codes should be invalid
        assertFalse(stationRegistryService.isValidStation("XYZ"));
    }

    @Test
    void shouldThrowExternalServiceExceptionWhenLoadingStationsFails() {
        // Mock the stations loading to trigger an exception
        when(fareEngineProperties.getServedStations()).thenThrow(new RuntimeException("Test message"));

        ExternalServiceException ex = assertThrows(
                ExternalServiceException.class,
                () -> stationRegistryService.refreshStations()
        );

        // Verifies that the messages are equal
        assertEquals("Unable to load served stations: Test message", ex.getMessage());
    }

    /**
     * Helper method to mock served stations and refresh the registry.
     */
    private void mockAirports(String... codes) {
        when(fareEngineProperties.getServedStations()).thenReturn(List.of(codes));
        stationRegistryService.refreshStations();
    }
}
