package com.utility.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.utility.dto.BillingCycle;
import com.utility.dto.RequestStatus;
import com.utility.dto.UtilityType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "connection_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionRequestEntity {

	@Id
    private String id;
    private String consumerId;
    private String consumerEmail;
    private UtilityType utilityType;
    private String tariffPlanId;
    private BillingCycle billingCycle;
    private RequestStatus status; // PENDING, APPROVED, REJECTED
    private LocalDate requestDate;
}
