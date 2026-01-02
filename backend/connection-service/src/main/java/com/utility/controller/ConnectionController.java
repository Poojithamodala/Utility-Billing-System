package com.utility.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.utility.dto.ConnectionRequest;
import com.utility.dto.ConnectionResponse;
import com.utility.service.ConnectionService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/connections")
@RequiredArgsConstructor
public class ConnectionController {

	private final ConnectionService service;

	@PostMapping
	public Mono<ConnectionResponse> create(@RequestBody @Valid ConnectionRequest request, ServerWebExchange exchange) {
		String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		return service.createConnection(request, authHeader);
	}

	@GetMapping
	public Flux<ConnectionResponse> all() {
		return service.getAllConnections();
	}

	@GetMapping("/consumer/{consumerId}")
	public Flux<ConnectionResponse> byConsumer(
			@PathVariable @NotBlank(message = "Consumer ID cannot be blank") String consumerId) {
		return service.getConnectionsByConsumer(consumerId);
	}

	@GetMapping("/{connectionId}")
	public Mono<ConnectionResponse> byId(@PathVariable String connectionId) {
		return service.getConnectionById(connectionId);
	}
}