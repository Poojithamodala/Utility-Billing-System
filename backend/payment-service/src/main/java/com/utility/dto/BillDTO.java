package com.utility.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class BillDTO {
    private String id;
    private String consumerId;
    private double totalAmount;
    private BillStatus status;
    private LocalDate dueDate;
    
}
