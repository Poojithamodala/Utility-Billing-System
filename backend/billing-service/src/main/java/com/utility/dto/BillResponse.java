package com.utility.dto;

import java.time.LocalDate;

import com.utility.model.BillStatus;
import com.utility.model.UtilityType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BillResponse {

    private String id;
    private String consumerId;
    private String connectionId;
    private String consumerEmail;
    private UtilityType utilityType;

    private double unitsConsumed;
    private double totalAmount;
    private BillStatus status;

    private LocalDate billDate;
    private LocalDate dueDate;
}
