package com.aafe.fareengine.service.distance;

import com.aafe.fareengine.model.Coordinates;
import com.aafe.fareengine.util.GeodesicUtil;
import org.springframework.stereotype.Component;

@Component("accurate")
public class AccurateDistanceStrategy implements DistanceStrategy {
    @Override
    public double calculate(Coordinates from, Coordinates to) {
        return GeodesicUtil.calculateGeodesicDistance(
                from.getLatitude(), from.getLongitude(),
                to.getLatitude(), to.getLongitude()
        );
    }
}
