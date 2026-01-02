package com.utility.dto;

import com.utility.model.PaymentMode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotBlank
    private String billId;

    @Positive
    private double amount;

    @NotNull
    private PaymentMode paymentMode;
}
