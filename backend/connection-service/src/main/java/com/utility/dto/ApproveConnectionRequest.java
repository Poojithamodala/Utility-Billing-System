package com.utility.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ApproveConnectionRequest {

    @NotBlank
    private String requestId;

    @NotBlank
    @Pattern(
        regexp = "^[A-Z0-9-]{6,20}$",
        message = "Meter number must be 6–20 chars (A-Z, 0-9, -)"
    )
    private String meterNumber;
}
