package com.utility.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.utility.dto.BillGenerateRequest;
import com.utility.dto.BillResponse;
import com.utility.dto.OutstandingBillResponse;
import com.utility.dto.OutstandingUpdateRequest;
import com.utility.model.BillStatus;
import com.utility.service.BillingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillingController {

	private final BillingService service;

	@PostMapping("/generate")
	public Mono<BillResponse> generate(@RequestBody @Valid BillGenerateRequest request, ServerWebExchange exchange) {
		String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		return service.generateBill(request, authHeader);
	}

	@GetMapping
	public Flux<BillResponse> getAllBills() {
		return service.getAllBills();
	}
	
	@GetMapping("/outstanding")
    public Flux<OutstandingBillResponse> outstandingBills() {
        return service.getOutstandingBills();
    }

	@PutMapping("/{billId}/outstanding")
	public Mono<Void> updateOutstanding(@PathVariable String billId, @RequestBody OutstandingUpdateRequest request) {
		return service.updateOutstandingAmount(billId, request.getOutstandingAmount(), request.getStatus());
	}

	@GetMapping("/consumer/{consumerId}")
	public Flux<BillResponse> consumerBills(@PathVariable String consumerId) {
		return service.getBillsForConsumer(consumerId, null);
	}

	@GetMapping("/{billId}")
	public Mono<BillResponse> getBillById(@PathVariable String billId) {
		return service.getBillById(billId);
	}

	@PutMapping("/{id}/status/{status}")
	public Mono<Void> updateStatus(@PathVariable String id, @PathVariable BillStatus status) {
		return service.updateBillStatus(id, status);
	}
}