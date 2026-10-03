package com.aafe.fareengine.service;

import com.aafe.fareengine.common.CacheNames;
import com.aafe.fareengine.dto.airport.AirportResponse;
import com.aafe.fareengine.exception.AirportNotFoundException;
import com.aafe.fareengine.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;


/**
 * Client responsible for retrieving airport details from an external API.
 * Results are cached to improve performance and reduce redundant API calls.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AirportClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${airport.api.base-url}")
    private String baseUrl;

    @Cacheable(value = CacheNames.AIRPORT_DETAILS_CACHE, key = "#airportCode")
    public AirportResponse getAirportDetails(String airportCode) {
        log.debug("Fetching airport details for code: {}", airportCode);
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(baseUrl + "/airports/{code}", airportCode)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response -> {
                        log.warn("Client error when fetching airport {}: {}", airportCode, response.statusCode());
                        return response.createException().flatMap(ex ->
                                Mono.error(new AirportNotFoundException(airportCode))
                        );
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, response -> {
                        log.error("Server error from airport service: {}", response.statusCode());
                        return response.createException().flatMap(ex ->
                                Mono.error(new ExternalServiceException("Airport service unavailable"))
                        );
                    })
                    .bodyToMono(AirportResponse.class)
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("Error fetching airport {}: {}", airportCode, ex.getMessage());
            throw new ExternalServiceException("Failed to fetch airport: " + airportCode);
        }
    }
}
