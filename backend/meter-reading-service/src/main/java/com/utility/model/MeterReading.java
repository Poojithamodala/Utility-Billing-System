package com.utility.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "meter_readings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeterReading {

    @Id
    private String id;

    @NotBlank
    private String connectionId;
    private String consumerEmail;

    @NotNull
    private UtilityType utilityType;
    
    @NotBlank
    private String meterNumber;

    @PositiveOrZero
    private double previousReading;

    @Positive
    private double currentReading;

    @Positive
    private double unitsConsumed;

    @NotNull
    private LocalDate readingDate;

    @NotNull
    private BillingCycle billingCycle;

    @NotNull
    private ReadingStatus status; // RECORDED / BILLED
}
