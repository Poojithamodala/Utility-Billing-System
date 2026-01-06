package com.utility.dto;

import java.time.LocalDate;

import com.utility.model.BillStatus;
import com.utility.model.UtilityType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OutstandingBillResponse {

    private String billId;
    private String consumerEmail;
    private UtilityType utilityType;

    private double totalAmount;
    private double paidSoFar;
    private double remainingAmount;

    private LocalDate dueDate;
    private BillStatus status;
}
