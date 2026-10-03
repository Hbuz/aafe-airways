package com.aafe.fareengine.dto.airport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AirportCoordinatesResponse {
    private LatitudeResponse latitude;
    private LongitudeResponse longitude;
}
