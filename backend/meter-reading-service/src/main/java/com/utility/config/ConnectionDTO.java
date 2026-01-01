package com.utility.config;

import com.utility.model.BillingCycle;
import com.utility.model.UtilityType;

import lombok.Data;

@Data
public class ConnectionDTO {

    private String id;
    private UtilityType utilityType;
    private BillingCycle billingCycle;
    private ConnectionStatus status;
    
    private String consumerId;

}
