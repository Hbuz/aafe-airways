package com.aafe.fareengine.service;

import com.aafe.fareengine.common.CacheNames;
import com.aafe.fareengine.config.WebClientConfig;
import com.aafe.fareengine.dto.airport.AirportResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

/**
 * Integration test to verify that AirportClient uses caching correctly.
 * It uses Caffeine cache configured via Spring Boot.
 */
@SpringBootTest
@EnableCaching
@Import({AirportClient.class, WebClientConfig.class})
@TestPropertySource(properties = {
        "spring.cache.type=caffeine",
        "airport.api.base-url=http://mock-api"
})
class AirportClientCachingTest {

    @MockitoBean
    private WebClient.Builder airportWebClient;

    @Mock
    private WebClient webClient;

    @Autowired
    private AirportClient airportClient;

    @Autowired
    private CacheManager cacheManager;

    private ResponseSpec responseSpecMock;

    @BeforeEach
    void setup() {
        // Clear the cache to ensure test isolation
        cacheManager.getCache(CacheNames.AIRPORT_DETAILS_CACHE).clear();
        // Mock the WebClient call chain for GET /airports/{code}
        mockWebClientChain();
    }

    @SuppressWarnings("unchecked")
    private void mockWebClientChain() {
        var requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        var requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        responseSpecMock = mock(WebClient.ResponseSpec.class);

        when(airportWebClient.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(eq("http://mock-api/airports/{code}"), eq("AMS"))).thenReturn(requestHeadersSpec);
        when(requestHeadersUriSpec.uri(eq("http://mock-api/airports/{code}"), eq("CDG"))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(eq(AirportResponse.class))).thenReturn(Mono.just(new AirportResponse()));
    }

    @Test
    void shouldCacheAirportDetailsForSameCode() {
        // Given a mocked response for airport AMS
        AirportResponse mockResponse = new AirportResponse();
        mockResponse.setIata("AMS");

        when(responseSpecMock.bodyToMono(eq(AirportResponse.class))).thenReturn(Mono.just(mockResponse));

        // When calling the client for the first time
        AirportResponse firstCall = airportClient.getAirportDetails("AMS");
        assertThat(firstCall.getIata()).isEqualTo("AMS");

        // And calling it again with the same code
        AirportResponse secondCall = airportClient.getAirportDetails("AMS");
        assertThat(secondCall.getIata()).isEqualTo("AMS");

        // Then the WebClient should only be invoked only once due to caching
        verify(webClient, times(1)).get();
    }

    @Test
    void shouldCallWebClientForDifferentAirportCodes() {
        // Given a mocked response for airport AMS
        AirportResponse response1 = new AirportResponse();
        response1.setIata("AMS");

        // And given a mocked response for airport AMS
        AirportResponse response2 = new AirportResponse();
        response2.setIata("CDG");

        when(responseSpecMock.bodyToMono(eq(AirportResponse.class)))
                .thenReturn(Mono.just(response1))
                .thenReturn(Mono.just(response2));

        // When calling the client for the airports
        airportClient.getAirportDetails("AMS");
        airportClient.getAirportDetails("CDG");

        // Should call WebClient twice for different codes
        verify(webClient, times(2)).get();
    }
}
