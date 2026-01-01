package com.utility.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "connections")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Connection {

    @Id
    private String id;
    private String consumerId;
    private UtilityType utilityType;
    private String meterNumber;
    private String tariffPlanId;
    private BillingCycleType billingCycle;
    private ConnectionStatus status;
    private LocalDate connectionDate;
}