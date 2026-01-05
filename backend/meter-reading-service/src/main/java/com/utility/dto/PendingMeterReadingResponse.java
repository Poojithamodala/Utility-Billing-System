package com.utility.dto;

import java.time.LocalDate;

import com.utility.model.BillingCycle;
import com.utility.model.UtilityType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PendingMeterReadingResponse {

    private String connectionId;
    private String consumerEmail;

    private UtilityType utilityType;
    private String meterNumber;
    private BillingCycle billingCycle;

    private LocalDate lastReadingDate;
    private Double lastReadingValue;
}
