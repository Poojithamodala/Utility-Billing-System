package com.utility.dto;

import java.time.LocalDate;

import com.utility.model.BillingCycleType;
import com.utility.model.ConnectionStatus;
import com.utility.model.UtilityType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConnectionResponse {

    private String id;
    private String consumerId;
    private UtilityType utilityType;
    private String meterNumber;
    private String tariffPlanId;
    private BillingCycleType billingCycle;
    private ConnectionStatus status;
    private LocalDate connectionDate;
}