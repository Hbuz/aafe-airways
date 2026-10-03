package com.aafe.fareengine.util;

import com.aafe.fareengine.model.Latitude;
import com.aafe.fareengine.model.Longitude;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


/**
 * Utility class for calculating distances between geographic coordinates using the Haversine formula.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HaversineUtil {

    private static final int EARTH_RADIUS_KM = 6371;

    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    public static double calculateHaversineDistance(Latitude lat1, Longitude lon1, Latitude lat2, Longitude lon2) {
        double[] from = CoordinateConverter.convertToDecimalCoordinates(lat1, lon1);
        double[] to = CoordinateConverter.convertToDecimalCoordinates(lat2, lon2);
        return haversine(from[0], from[1], to[0], to[1]);
    }
}
