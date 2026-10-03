package com.aafe.fareengine.controller;

import com.aafe.fareengine.config.FareEngineProperties;
import com.aafe.fareengine.dto.airport.AirportCoordinatesResponse;
import com.aafe.fareengine.dto.airport.AirportLocationResponse;
import com.aafe.fareengine.dto.airport.AirportResponse;
import com.aafe.fareengine.dto.airport.LatitudeResponse;
import com.aafe.fareengine.dto.airport.LongitudeResponse;
import com.aafe.fareengine.model.Itinerary;
import com.aafe.fareengine.service.AirportClient;
import com.aafe.fareengine.service.StationRegistryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = FareEngineProperties.class)
@TestPropertySource("classpath:application-test.yml")
class ItineraryControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FareEngineProperties fareEngineProperties;

    @MockitoBean
    private StationRegistryService stationRegistryService;
    @MockitoBean
    private AirportClient airportClient;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/itinerary";
    }


    @BeforeEach
    void setupMocks() {
        // Mock valid stations
        when(stationRegistryService.isValidStation("AMS")).thenReturn(true);
        when(stationRegistryService.isValidStation("BCN")).thenReturn(true);

        AirportLocationResponse amsLocation = new AirportLocationResponse();
        amsLocation.setCoordinates(new AirportCoordinatesResponse(
                new LatitudeResponse(52, 18, 38, "N"),
                new LongitudeResponse(4, 45, 48, "E")));// Mock airport details

        AirportLocationResponse bcnLocation = new AirportLocationResponse();
        bcnLocation.setCoordinates(new AirportCoordinatesResponse(
                new LatitudeResponse(41, 17, 51, "N"),
                new LongitudeResponse(2, 4, 59, "E")));

        AirportResponse amsResponse = new AirportResponse();
        amsResponse.setLocation(amsLocation);

        AirportResponse bcnResponse = new AirportResponse();
        bcnResponse.setLocation(bcnLocation);

        when(airportClient.getAirportDetails("AMS"))
                .thenReturn(amsResponse);
        when(airportClient.getAirportDetails("BCN"))
                .thenReturn(bcnResponse);
    }

    @Test
    void shouldReturnFareForValidItinerary() {
        String url = getBaseUrl() + "?airport=AMS&airport=BCN";

        ResponseEntity<Itinerary> response = restTemplate.getForEntity(url, Itinerary.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Itinerary itinerary = response.getBody();
        assertNotNull(itinerary);
        assertEquals(List.of("AMS", "BCN"), itinerary.getAirports());
        assertTrue(itinerary.getDistance() > 0);
        assertTrue(itinerary.getFare().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(itinerary.getTax().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void shouldReturnBadRequestForMissingAirports() {
        String url = getBaseUrl();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldReturnErrorWhenItineraryStartsWithForbiddenAirport() {
        // Given
        when(stationRegistryService.isValidStation("BHD")).thenReturn(true);
        when(stationRegistryService.isValidStation("CDG")).thenReturn(true);

        String url = getBaseUrl() + "?airport=BHD&airport=CDG";

        // When
        var response = restTemplate.getForEntity(url, Map.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        var body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("status"));
        assertEquals("Constraint violation", body.get("error"));
        assertTrue(((String) body.get("message")).contains("Itinerary cannot start in BHD"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void shouldReturnErrorWhenItineraryEndsWithForbiddenAirport() {
        when(stationRegistryService.isValidStation("AMS")).thenReturn(true);
        when(stationRegistryService.isValidStation("CAG")).thenReturn(true);

        String url = getBaseUrl() + "?airport=AMS&airport=CAG";

        var response = restTemplate.getForEntity(url, Map.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        var body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("status"));
        assertEquals("Constraint violation", body.get("error"));
        assertTrue(((String) body.get("message")).contains("Itinerary cannot end in CAG"));
        assertNotNull(body.get("timestamp"));
    }

}
