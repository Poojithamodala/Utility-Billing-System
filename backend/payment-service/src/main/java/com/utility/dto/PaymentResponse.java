package com.utility.dto;

import java.time.LocalDate;

import com.utility.model.PaymentMode;
import com.utility.model.PaymentStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {
    private String paymentId;
    private String billId;
    private double amountPaid;
    private PaymentMode paymentMode;
    private PaymentStatus paymentStatus;
    private LocalDate paymentDate;
    private String referenceNumber;
}