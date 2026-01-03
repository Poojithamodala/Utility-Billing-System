package com.utility.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillGeneratedEvent {
    private String billId;
    private String consumerEmail;
    private double amount;
    private LocalDate dueDate;
}