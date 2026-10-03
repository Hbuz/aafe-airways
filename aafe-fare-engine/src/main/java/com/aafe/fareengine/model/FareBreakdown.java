package com.aafe.fareengine.model;

import java.math.BigDecimal;

public record FareBreakdown(BigDecimal baseFare,
                            BigDecimal tax,
                            BigDecimal totalFare,
                            BigDecimal distanceCost,
                            BigDecimal stopoverCost,
                            BigDecimal fixedBaseCost,
                            BigDecimal serviceCharge
) {
}