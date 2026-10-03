package com.aafe.fareengine.util;

import com.aafe.fareengine.model.Latitude;
import com.aafe.fareengine.model.Longitude;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;


/**
 * Utility class for calculating distances between geographic coordinates using the Geodesic method.
 */
@NoArgsConstructor(access = AccessLevel. PRIVATE)
public final class GeodesicUtil {

    public static double calculateGeodesicDistance(Latitude lat1, Longitude lon1, Latitude lat2, Longitude lon2) {
        double[] from = CoordinateConverter.convertToDecimalCoordinates(lat1, lon1);
        double[] to = CoordinateConverter.convertToDecimalCoordinates(lat2, lon2);
        GeodesicData result = Geodesic.WGS84.Inverse(from[0], from[1], to[0], to[1]);
        return result.s12 / 1000.0;
    }
}
