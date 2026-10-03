package com.aafe.fareengine.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Longitude {
    private int degrees;
    private int minutes;
    private int seconds;
    private String direction; // 'E' or 'W'
}