package com.aafe.fareengine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Itinerary {
    private List<String> airports;
    private BigDecimal fare;
    private BigDecimal tax;
    private int distance; // The distance in kilometers
}
