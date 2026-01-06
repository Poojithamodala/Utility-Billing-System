package com.utility.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.utility.dto.MeterReadingRequest;
import com.utility.dto.MeterReadingResponse;
import com.utility.dto.PendingMeterReadingResponse;
import com.utility.dto.UtilityConsumptionResponse;
import com.utility.service.MeterReadingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/meter-readings")
@RequiredArgsConstructor
@Validated
public class MeterReadingController {

	private final MeterReadingService service;

	@PostMapping
	public Mono<MeterReadingResponse> record(@RequestBody @Valid MeterReadingRequest request, ServerWebExchange exchange) {
		String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		return service.recordReading(request, authHeader);
	}
	
	@GetMapping("/pending")
	public Flux<PendingMeterReadingResponse> pendingReadings(ServerWebExchange exchange) {
		String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		return service.getPendingMeterReadings(authHeader);
	}

	@GetMapping("/connection/{connectionId}")
	public Flux<MeterReadingResponse> byConnection(@PathVariable String connectionId, ServerWebExchange exchange) {
		String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		return service.getReadingsByConnection(connectionId, authHeader);
	}

	@GetMapping
	public Flux<MeterReadingResponse> all() {
		return service.getAllReadings();
	}

	@GetMapping("/{readingId}")
	public Mono<MeterReadingResponse> getById(@PathVariable String readingId) {
		return service.getReadingById(readingId);
	}
	
	@PutMapping("/{id}/mark-billed")
	public Mono<Void> markAsBilled(@PathVariable String id) {
	    return service.markReadingAsBilled(id);
	}
	
	@GetMapping("/consumption/utility")
    public Flux<UtilityConsumptionResponse> utilityConsumption() {
        return service.getUtilityWiseConsumption();
    }
}