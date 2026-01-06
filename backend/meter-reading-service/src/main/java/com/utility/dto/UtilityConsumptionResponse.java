package com.utility.dto;

import com.utility.model.UtilityType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UtilityConsumptionResponse {
    private UtilityType utilityType;
    private double totalUnitsConsumed;
}

