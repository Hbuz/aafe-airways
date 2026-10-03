package com.aafe.fareengine.service.distance;

import com.aafe.fareengine.model.Coordinates;
import com.aafe.fareengine.util.HaversineUtil;
import org.springframework.stereotype.Component;

@Component("haversine")
public class HaversineDistanceStrategy implements DistanceStrategy {
    @Override
    public double calculate(Coordinates from, Coordinates to) {
        return HaversineUtil.calculateHaversineDistance(
                from.getLatitude(), from.getLongitude(),
                to.getLatitude(), to.getLongitude()
        );
    }
}
