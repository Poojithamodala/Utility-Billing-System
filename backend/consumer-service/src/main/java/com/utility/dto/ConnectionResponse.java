package com.utility.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionResponse {

    private String id;
    private String consumerId;
    private UtilityType utilityType;
    private String meterNumber;
    private String tariffPlanId;
    private BillingCycle billingCycle;
    private ConnectionStatus status;
    private LocalDate connectionDate;
    
    private String consumerEmail;
}