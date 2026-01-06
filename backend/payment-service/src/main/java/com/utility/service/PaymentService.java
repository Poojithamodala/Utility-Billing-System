package com.utility.service;

import com.utility.dto.MonthlyRevenueResponse;
import com.utility.dto.PaymentRequest;
import com.utility.dto.PaymentResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PaymentService {
	Mono<PaymentResponse> makePayment(PaymentRequest request, String authHeader);
	Flux<PaymentResponse> getAllPayments();
	Flux<PaymentResponse> getPaymentsForBill(String billId);
	Flux<PaymentResponse> getPaymentsForConsumer(String consumerId);
	
	Flux<MonthlyRevenueResponse> getMonthlyRevenue();
}
