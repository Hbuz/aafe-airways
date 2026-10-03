package com.aafe.fareengine.config;

import com.aafe.fareengine.service.distance.DistanceStrategy;
import com.aafe.fareengine.service.distance.HaversineDistanceStrategy;
import com.aafe.fareengine.service.distance.AccurateDistanceStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Configuration for selecting the appropriate distance calculation strategy based on the value
 * defined in {@link FareEngineProperties}.
 */
@Configuration
public class DistanceStrategyConfig {

    @Bean
    public DistanceStrategy distanceStrategy(
            FareEngineProperties properties,
            HaversineDistanceStrategy haversine,
            AccurateDistanceStrategy accurate
    ) {
        return "accurate".equalsIgnoreCase(properties.getDistanceMethod()) ? accurate : haversine;
    }
}
