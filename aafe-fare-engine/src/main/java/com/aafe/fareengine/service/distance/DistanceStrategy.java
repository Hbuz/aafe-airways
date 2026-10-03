package com.aafe.fareengine.service.distance;

import com.aafe.fareengine.model.Coordinates;

public interface DistanceStrategy {
    double calculate(Coordinates from, Coordinates to);
}
