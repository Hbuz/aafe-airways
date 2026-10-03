package com.aafe.fareengine.dto.airport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LongitudeResponse {
    private int degrees;
    private int minutes;
    private int seconds;
    private String direction; // 'E' or 'W'
}