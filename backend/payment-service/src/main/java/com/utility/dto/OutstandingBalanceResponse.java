package com.utility.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OutstandingBalanceResponse {
    private String billId;
    private double billAmount;
    private double totalPaid;
    private double outstandingAmount;
}
