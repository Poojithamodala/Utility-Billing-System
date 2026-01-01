package com.utility.dto;

import java.util.List;

import com.utility.model.UtilityType;

import lombok.Data;

@Data
public class TariffPlanDTO {
    private String id;
    private UtilityType utilityType;
    private String name;
    private double fixedCharge;
    private double taxPercentage;
    private List<TariffSlab> slabs;
}
