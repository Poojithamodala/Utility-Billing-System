package com.utility.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillGenerateRequest {
    @NotBlank(message="Meter Reading Id is required")
    private String meterReadingId;
}