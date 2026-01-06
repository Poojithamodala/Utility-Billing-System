package com.utility.dto;

import com.utility.model.BillStatus;

import lombok.Data;

@Data
public class OutstandingUpdateRequest {
    private double outstandingAmount;
    private BillStatus status;
}

