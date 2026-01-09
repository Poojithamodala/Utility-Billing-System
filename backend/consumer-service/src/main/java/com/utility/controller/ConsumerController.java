package com.utility.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import com.utility.dto.ConnectionRequestByConsumer;
import com.utility.dto.ConsumerGrowthResponse;
import com.utility.dto.ConsumerRegistrationRequestResponse;
import com.utility.dto.ConsumerRequest;
import com.utility.dto.ConsumerResponse;
import com.utility.dto.RejectConsumerRequest;
import com.utility.dto.RequestStatus;
import com.utility.model.ConnectionRequestEntity;
import com.utility.security.JwtUtil;
import com.utility.service.ConsumerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/consumers")
@RequiredArgsConstructor
public class ConsumerController {
	private static final String MESSAGE_KEY = "message";

	private final ConsumerService consumerService;
	private final JwtUtil jwtUtil;

	@PostMapping("/request")
	public Mono<Map<String, String>> requestConsumerRegistration(@Valid @RequestBody ConsumerRequest request) {
		return consumerService.submitRegistrationRequest(request)
				.thenReturn(Map.of(MESSAGE_KEY, "Registration request submitted"));
	}
	
	@GetMapping("/requests")
	public Flux<ConsumerRegistrationRequestResponse> getAllRequests(Authentication authentication) {
		return consumerService.getAllRegistrationRequests();
	}
	
	@PostMapping("/requests/{id}/approve")
	public Mono<Map<String, String>> approveRequest(@PathVariable String id, ServerWebExchange exchange) {
		String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		return consumerService.approveRequest(id, authHeader)
				.thenReturn(Map.of(MESSAGE_KEY, "Consumer approved successfully"));
	}
	
	@PostMapping("/requests/{requestId}/reject")
	public Mono<Map<String, String>> rejectRequest(@PathVariable String requestId,  @Valid @RequestBody RejectConsumerRequest request, Authentication authentication) {
		return consumerService.rejectRequest(requestId, request.getReason())
				.thenReturn(Map.of(MESSAGE_KEY, "Consumer registration request rejected"));
	}
	
	@PostMapping("/request-connection")
	public Mono<Map<String, String>> requestConnection(@RequestBody @Valid ConnectionRequestByConsumer request,
			ServerWebExchange exchange) {
		String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		return consumerService.requestConnection(request, authHeader)
		        .thenReturn(Map.of(
		            "message",
		            "Connection request submitted successfully. Please wait for Billing Officer approval."
		        ));
	}
	
	@GetMapping("/connection-requests")
	public Flux<ConnectionRequestEntity> getPendingRequests(@RequestParam RequestStatus status) {
		return consumerService.getRequestsByStatus(status);
	}
	
	@GetMapping("/connection-requests/{id}")
	public Mono<ConnectionRequestEntity> getRequestById(@PathVariable String id) {
		return consumerService.getRequestById(id);
	}

	@PatchMapping("/connection-requests/{id}/status")
	public Mono<Void> updateRequestStatus(@PathVariable String id, @RequestBody RequestStatus status) {
		return consumerService.updateRequestStatus(id, status);
	}

	// create consumer
	@PostMapping
	public Mono<Map<String, String>> createConsumer(@Valid @RequestBody ConsumerRequest request,
			ServerWebExchange exchange, Authentication authentication) {
		String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

		return consumerService.createConsumer(authHeader, request).map(consumerResponse -> Map.of("message",
				"Consumer created successfully", "consumerId", consumerResponse.getId()));
	}

	// view all consumers
	@GetMapping
	public Flux<ConsumerResponse> getAllConsumers(Authentication authentication) {
		return consumerService.getAllConsumers();
	}

	// view consumer by ID
	@GetMapping("/{id}")
	public Mono<ConsumerResponse> getConsumer(@PathVariable String id, Authentication authentication) {
		return consumerService.getConsumerById(id);
	}

	// update consumer by admin
	@PutMapping("/{id}")
	public Mono<ConsumerResponse> updateConsumer(@PathVariable String id, @RequestBody ConsumerRequest dto,
			Authentication authentication) {
		return consumerService.updateConsumer(id, dto);
	}

	// delete consumer by admin
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Map<String, String>>> deleteConsumer(@PathVariable String id,
			Authentication authentication) {
		if (id == null || id.isBlank()) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Consumer ID is required"));
		}
		return consumerService.deleteConsumer(id)
				.thenReturn(ResponseEntity.ok(Map.of(MESSAGE_KEY, "Consumer deleted successfully")));
	}

	@GetMapping("/profile")
	public Mono<ConsumerResponse> myProfile(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return Mono.error(new RuntimeException("Missing Authorization header"));
		}

		String token = authHeader.substring(7);
		String username = jwtUtil.extractUsername(token);

		return consumerService.getMyProfile(username);
	}
	
	@GetMapping("/reports/growth")
    public Flux<ConsumerGrowthResponse> consumerGrowth() {
        return consumerService.getConsumerGrowth();
    }
}