package com.utility.dto;

import com.utility.model.UtilityType;

import lombok.Data;

@Data
public class ConnectionDTO {
    private String id;
    private String consumerId;
    private UtilityType utilityType;
    private String tariffPlanId;
    private BillingCycle billingCycle;
    private ConnectionStatus status;
    
    private String consumerEmail;
}
