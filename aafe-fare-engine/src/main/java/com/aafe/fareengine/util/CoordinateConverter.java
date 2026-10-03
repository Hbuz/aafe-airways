package com.aafe.fareengine.util;

import com.aafe.fareengine.model.Latitude;
import com.aafe.fareengine.model.Longitude;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


/**
 * Utility class for converting geographic coordinates from degrees-minutes-seconds (DMS) format to decimal degrees.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CoordinateConverter {

    public static double[] convertToDecimalCoordinates(Latitude lat, Longitude lon) {
        double latDecimal = toDecimalDegrees(lat.getDegrees(), lat.getMinutes(), lat.getSeconds(), lat.getDirection());
        double lonDecimal = toDecimalDegrees(lon.getDegrees(), lon.getMinutes(), lon.getSeconds(), lon.getDirection());
        return new double[]{latDecimal, lonDecimal};
    }

    public static double toDecimalDegrees(int degrees, int minutes, int seconds, String direction) {
        // Convert DMS to decimal degrees
        double decimal = degrees + minutes / 60.0 + seconds / 3600.0;

        // Add negative sign for southern and western hemispheres
        if ("S".equalsIgnoreCase(direction) || "W".equalsIgnoreCase(direction)) {
            decimal *= -1;
        }
        return decimal;
    }
}
