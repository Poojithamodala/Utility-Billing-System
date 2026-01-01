package com.utility.service;

import com.utility.dto.BillGenerateRequest;
import com.utility.dto.BillResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BillingService {
	Mono<BillResponse> generateBill(BillGenerateRequest request, String authHeader);
	Flux<BillResponse> getBillsForConsumer(String consumerId, String authHeader);
}
