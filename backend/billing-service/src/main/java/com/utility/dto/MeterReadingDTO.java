package com.utility.dto;

import java.time.LocalDate;

import com.utility.model.UtilityType;

import lombok.Data;

@Data
public class MeterReadingDTO {
    private String id;
    private String connectionId;
    private UtilityType utilityType;
    private double previousReading;
    private double currentReading;
    private double unitsConsumed;
    private LocalDate readingDate;
    private BillingCycle billingCycle;
    private ReadingStatus status;
}
