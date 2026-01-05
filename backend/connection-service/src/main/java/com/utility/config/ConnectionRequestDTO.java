package com.utility.config;

import com.utility.model.UtilityType;

import lombok.Data;

@Data
public class ConnectionRequestDTO {

    private String id;
    private String consumerId;
    private UtilityType utilityType;
    private String tariffPlanId;
    private BillingCycle billingCycle;
    private RequestStatus status;
}