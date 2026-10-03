package com.aafe.fareengine.validation;

import com.aafe.fareengine.config.FareEngineProperties;
import com.aafe.fareengine.service.AirportClient;
import com.aafe.fareengine.service.StationRegistryService;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = FareEngineProperties.class)
@TestPropertySource("classpath:application-test.yml")
class ItineraryValidatorTest {

    @Autowired
    private StationRegistryService stationRegistryService;

    @Autowired
    private FareEngineProperties fareEngineProperties;

    @MockitoBean
    private AirportClient airportClient;

    private ItineraryValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        // Initialize the validator with required dependencies
        validator = new ItineraryValidator(stationRegistryService, fareEngineProperties);
        context = mock(ConstraintValidatorContext.class);

        // Mock the validation context to allow custom violation messages
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);

        // Refresh station data before each test
        stationRegistryService.refreshStations();
    }

    @Test
    void shouldLoadForbiddenAirportsFromProperties() {
        // Verifies that forbidden airports are correctly loaded from the configuration
        assertTrue(fareEngineProperties.getForbiddenStarts().contains("BHD"));
        assertTrue(fareEngineProperties.getForbiddenEnds().contains("CAG"));
    }

    @Test
    void shouldFailWhenItineraryIsNull() {
        // Null itineraries should be considered invalid
        boolean result = validator.isValid(null, context);
        assertFalse(result);
    }

    @Test
    void shouldFailWhenItineraryHasLessThanTwoAirports() {
        // Itineraries with fewer than two airports are invalid
        boolean result = validator.isValid(List.of("AMS"), context);
        assertFalse(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"BHD", "XYZ"})
    void shouldFailWhenItineraryStartsWithForbiddenOrUnknownAirport(String start) {
        // Itineraries starting with a forbidden airport should be invalid
        // Assuming that "XYZ" is not contained in the served-stations list
        boolean result = validator.isValid(List.of(start, "CDG"), context);
        assertFalse(result);
    }

    @Test
    void shouldFailWhenItineraryEndsWithForbiddenAirport() {
        // Itineraries ending with a forbidden airport should be invalid
        boolean result = validator.isValid(List.of("AMS", "CAG"), context);
        assertFalse(result);
    }

    @Test
    void shouldPassWhenItineraryIsValid() {
        // Valid itineraries should pass validation
        boolean result = validator.isValid(List.of("AMS", "CDG"), context);
        assertTrue(result);
    }
}
