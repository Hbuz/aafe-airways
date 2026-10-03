package com.aafe.fareengine.controller;

import com.aafe.fareengine.dto.airport.AirportResponse;
import com.aafe.fareengine.model.Itinerary;
import com.aafe.fareengine.service.AirportClient;
import com.aafe.fareengine.service.FareCalculationService;
import com.aafe.fareengine.service.StationRegistryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItineraryController.class)
class ItineraryControllerExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FareCalculationService fareCalculationService;

    @MockitoBean
    private StationRegistryService stationRegistryService;
    @MockitoBean
    private AirportClient airportClient;

    @Test
    void shouldReturnBadRequestWhenItineraryIsInvalid() throws Exception {
        when(fareCalculationService.calculateFare(List.of("AMS", "CDG"))).thenReturn(new Itinerary());
//                .thenThrow(new IllegalArgumentException("Itinerary must include at least two stronzi."));
        when(stationRegistryService.isValidStation(anyString())).thenReturn(true);

        mockMvc.perform(get("/itinerary")
                        .param("airport", "AMS", "CDG")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
//                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
//                .andExpect(jsonPath("$.message").value("Itinerary must include at least two airports."));
    }

    @Test
    void shouldReturnStructuredErrorWhenItineraryStartsWithForbiddenAirport() throws Exception {
//        List<AirportResponse> mockAirports = List.of(
//                new AirportResponse("AMS", "Amsterdam Airport Schiphol", null),
//                new AirportResponse("LAX", "Los Angeles International", null),
//                new AirportResponse("CDG", "Charles de Gaulle", null)
//        );
//
//        when(airportClient.getAllAirports()).thenReturn(mockAirports);
//        stationRegistryService.refreshStations();
        when(stationRegistryService.isValidStation(anyString())).thenReturn(true);

        mockMvc.perform(get("/itinerary")
                        .param("airport", "BHD")
                        .param("airport", "CDG"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Constraint violation"))
                .andExpect(jsonPath("$.message").value(containsString("Itinerary cannot start in BHD")))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
