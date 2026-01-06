package com.utility.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "bills")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bill {

	@Id
    private String id;

    private String consumerId;
    private String connectionId;
    private String consumerEmail;

    private UtilityType utilityType;
    private String meterReadingId;

    private double unitsConsumed;
    private double energyCharge;
    private double fixedCharge;
    private double taxAmount;

    private double totalAmount;
    private double outstandingAmount;

    private BillStatus status;
    private LocalDate billDate;
    private LocalDate dueDate;
}