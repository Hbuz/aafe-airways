package com.aafe.fareengine.controller;

import com.aafe.fareengine.model.Itinerary;
import com.aafe.fareengine.service.FareCalculationService;
import com.aafe.fareengine.validation.ValidItinerary;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * REST controller for fare calculation based on a list of airport codes.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/itinerary")
@Validated
public class ItineraryController {

    private final FareCalculationService fareCalculationService;

    @GetMapping
    public Itinerary calculateFare(@RequestParam("airport") @ValidItinerary List<String> airports) {
        return fareCalculationService.calculateFare(airports);
    }
}
