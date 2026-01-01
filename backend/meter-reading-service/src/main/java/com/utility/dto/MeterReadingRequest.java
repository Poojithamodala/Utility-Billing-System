package com.utility.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class MeterReadingRequest {

    @NotBlank(message = "Connection ID is required")
    private String connectionId;

    @Positive(message = "Current reading must be positive")
    private double currentReading;

    @NotNull(message = "Reading date is required")
    private LocalDate readingDate;
}