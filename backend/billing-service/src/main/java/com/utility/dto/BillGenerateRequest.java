package com.utility.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BillGenerateRequest {
    @NotBlank(message="Meter Reading Id is required")
    private String meterReadingId;
}