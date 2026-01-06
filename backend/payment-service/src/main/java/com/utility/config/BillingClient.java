package com.utility.config;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.utility.dto.BillDTO;
import com.utility.dto.BillStatus;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BillingClient {

	private final WebClient billingWebClient;

	public Mono<BillDTO> getBill(String billId, String authHeader) {
		return billingWebClient.get().uri("/bills/{id}", billId).header(HttpHeaders.AUTHORIZATION, authHeader)
				.retrieve().bodyToMono(BillDTO.class);
	}

	public Mono<Void> updateBillStatus(String billId, BillStatus status, String authHeader) {
		return billingWebClient.put().uri("/bills/{id}/status/{status}", billId, status)
				.header(HttpHeaders.AUTHORIZATION, authHeader).retrieve().bodyToMono(Void.class);
	}

	public Mono<Void> updateBillOutstanding(String billId, double outstandingAmount, BillStatus status,
			String authHeader) {
		return billingWebClient.put().uri("/bills/{id}/outstanding", billId)
				.header(HttpHeaders.AUTHORIZATION, authHeader)
				.bodyValue(Map.of("outstandingAmount", outstandingAmount, "status", status)).retrieve()
				.bodyToMono(Void.class);
	}
}
