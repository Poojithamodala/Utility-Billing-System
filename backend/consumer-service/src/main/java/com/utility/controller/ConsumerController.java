package com.utility.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import com.utility.dto.ConsumerRequest;
import com.utility.dto.ConsumerResponse;
import com.utility.service.ConsumerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/consumers")
@RequiredArgsConstructor
public class ConsumerController {

	private final ConsumerService consumerService;

	// create consumer
	@PostMapping
	public Mono<Map<String, String>> createConsumer(@Valid @RequestBody ConsumerRequest request,
			ServerWebExchange exchange, Authentication authentication) {

		if (!hasRole(authentication, "ADMIN")) {
			return Mono.error(new RuntimeException("Only admin can create consumers"));
		}

		String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

		return consumerService.createConsumer(authHeader, request).map(consumerResponse -> Map.of("message",
				"Consumer created successfully", "consumerId", consumerResponse.getId()));
	}

	// view all consumers
	@GetMapping
	public Flux<ConsumerResponse> getAllConsumers(Authentication authentication) {
		if (!hasRole(authentication, "ADMIN")) {
			return Flux.error(new RuntimeException("Only admin can view all consumers"));
		}
		return consumerService.getAllConsumers();
	}

	// view consumer by ID
	@GetMapping("/{id}")
	public Mono<ConsumerResponse> getConsumer(@PathVariable String id, Authentication authentication) {
		if (!hasRole(authentication, "ADMIN")) {
			return Mono.error(new RuntimeException("Only admin can view consumers by ID"));
		}
		return consumerService.getConsumerById(id);
	}

	// update consumer by admin
	@PutMapping("/{id}")
	public Mono<ConsumerResponse> updateConsumer(@PathVariable String id, @RequestBody ConsumerRequest dto,
			Authentication authentication) {
		if (!hasRole(authentication, "ADMIN")) {
			return Mono.error(new RuntimeException("Only admin can update consumers"));
		}
		return consumerService.updateConsumer(id, dto);
	}

	// delete consumer by admin
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Map<String, String>>> deleteConsumer(@PathVariable String id,
			Authentication authentication) {
		if (!hasRole(authentication, "ADMIN")) {
			return Mono.error(new RuntimeException("Only admin can delete consumers"));
		}
		if (id == null || id.isBlank()) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Consumer ID is required"));
		}
		return consumerService.deleteConsumer(id)
				.thenReturn(ResponseEntity.ok(Map.of("message", "Consumer deleted successfully")));
	}

	// view own profile
	@GetMapping("/profile")
	public Mono<ConsumerResponse> myProfile(Authentication authentication) {
		return consumerService.getMyProfile(authentication.getName());
	}

	// check role
	private boolean hasRole(Authentication auth, String role) {
		return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
	}
}