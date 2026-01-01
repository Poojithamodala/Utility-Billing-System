package com.utility.dto;

import com.utility.model.BillingCycleType;
import com.utility.model.UtilityType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ConnectionRequest {

	@NotBlank(message = "Consumer ID is required")
    private String consumerId;

    @NotNull(message = "Utility type is required")
    private UtilityType utilityType;

    @NotBlank(message = "Meter number is required")
    @Pattern(
            regexp = "^[A-Z0-9-]{6,20}$",
            message = "Meter number must be 6–20 chars (A-Z, 0-9, -)"
        )
    private String meterNumber;

    @NotBlank(message = "Tariff plan ID is required")
    private String tariffPlanId;

    @NotNull(message = "Billing cycle is required")
    private BillingCycleType billingCycle;
}