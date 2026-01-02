package com.utility.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.utility.dto.PaymentRequest;
import com.utility.dto.PaymentResponse;
import com.utility.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService service;

	@PostMapping
	public Mono<PaymentResponse> makePayment(@RequestBody @Valid PaymentRequest request, ServerWebExchange exchange) {
		String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		return service.makePayment(request, authHeader);
	}

	@GetMapping("/bill/{billId}")
	public Flux<PaymentResponse> billPayments(@PathVariable String billId) {
		return service.getPaymentsForBill(billId);
	}

	@GetMapping("/consumer/{consumerId}")
	public Flux<PaymentResponse> consumerPayments(@PathVariable String consumerId) {
		return service.getPaymentsForConsumer(consumerId);
	}
}
