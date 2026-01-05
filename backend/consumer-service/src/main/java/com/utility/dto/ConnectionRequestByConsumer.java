package com.utility.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConnectionRequestByConsumer {

    @NotNull
    private UtilityType utilityType;

    @NotBlank
    private String tariffPlanId;

    @NotNull
    private BillingCycle billingCycle;
}
