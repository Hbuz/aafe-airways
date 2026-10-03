package com.aafe.fareengine.validation;

import com.aafe.fareengine.config.FareEngineProperties;
import com.aafe.fareengine.service.StationRegistryService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

import java.util.List;


/**
 * Custom validator for validating an itinerary consisting of a list of airport codes.
 * Ensures the itinerary:
 * - Contains at least two airports
 * - Starts and ends with valid stations
 * - Does not start or end with forbidden airports
 */
@RequiredArgsConstructor
public class ItineraryValidator implements ConstraintValidator<ValidItinerary, List<String>> {

    private final StationRegistryService stationRegistryService;
    private final FareEngineProperties properties;

    @Override
    public boolean isValid(List<String> airports, ConstraintValidatorContext context) {
        if (airports == null || airports.size() < 2) {
            return buildViolation(context, "Itinerary must include at least two airports.");
        }

        String start = airports.get(0);
        String end = airports.get(airports.size() - 1);

        // Validate departure and arrival airports
        if (!stationRegistryService.isValidStation(start)) {
            return buildViolation(context, "Unknown departure airport: " + start);
        }

        if (!stationRegistryService.isValidStation(end)) {
            return buildViolation(context, "Unknown arrival airport: " + end);
        }

        // Check if the itinerary starts or ends with a forbidden airport
        if (properties.getForbiddenStarts().contains(start)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Itinerary cannot start in " + start)
                    .addConstraintViolation();
            return false;
        }

        if (properties.getForbiddenEnds().contains(end)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Itinerary cannot end in " + end)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

    private boolean buildViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return false;
    }
}
