package com.utility.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RejectConsumerRequest {
    @NotBlank
    private String reason;
}
