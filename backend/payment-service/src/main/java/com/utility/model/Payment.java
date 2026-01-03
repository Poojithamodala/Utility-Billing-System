package com.utility.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    private String id;

    private String billId;
    private String consumerId;
    private String consumerEmail;

    private double amountPaid;

    private PaymentMode paymentMode;     // ONLINE / CASH / UPI
    private PaymentStatus paymentStatus; // SUCCESS / FAILED

    private LocalDate paymentDate;

    private String referenceNumber; // mock txn id
}
