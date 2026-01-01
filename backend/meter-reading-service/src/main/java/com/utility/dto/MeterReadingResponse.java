package com.utility.dto;

import java.time.LocalDate;

import com.utility.model.BillingCycle;
import com.utility.model.ReadingStatus;
import com.utility.model.UtilityType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MeterReadingResponse {

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