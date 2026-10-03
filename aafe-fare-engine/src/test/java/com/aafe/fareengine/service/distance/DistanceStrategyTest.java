package com.aafe.fareengine.service.distance;

import com.aafe.fareengine.model.Coordinates;
import com.aafe.fareengine.model.Latitude;
import com.aafe.fareengine.model.Longitude;
import com.aafe.fareengine.util.GeodesicUtil;
import com.aafe.fareengine.util.HaversineUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DistanceStrategyTest {

    @Test
    void shouldReturnSimilarResultsForHaversineAndGeodesicDistance() {
        // Given the coordinates for the airports AMS and LAX
        Coordinates ams = new Coordinates(
                new Latitude(52, 18, 0, "N"),
                new Longitude(4, 45, 0, "E")
        );
        Coordinates lax = new Coordinates(
                new Latitude(33, 56, 0, "N"),
                new Longitude(118, 24, 0, "W")
        );

        // When calculating distances using two different strategies
        double haversineDistance = HaversineUtil.calculateHaversineDistance(ams.getLatitude(), ams.getLongitude(),
                lax.getLatitude(), lax.getLongitude());

        double geodesicDistance = GeodesicUtil.calculateGeodesicDistance(ams.getLatitude(), ams.getLongitude(),
                lax.getLatitude(), lax.getLongitude());

        // Then the distances should be almost equal within a margin
        assertThat(haversineDistance).isNotEqualTo(geodesicDistance);
        assertThat(Math.abs(haversineDistance - geodesicDistance)).isLessThan(100); // within 100 km
    }
}
