package com.aafe.fareengine.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Latitude {
    private int degrees;
    private int minutes;
    private int seconds;
    private String direction; // 'N' or 'S'
}
