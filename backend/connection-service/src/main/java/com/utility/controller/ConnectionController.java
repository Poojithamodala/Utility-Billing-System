package com.utility.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.utility.dto.ApproveConnectionRequest;
import com.utility.dto.ConnectionRequest;
import com.utility.dto.ConnectionResponse;
import com.utility.model.Connection;
import com.utility.model.UtilityType;
import com.utility.service.ConnectionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/connections")
@RequiredArgsConstructor
public class ConnectionController {

	private final ConnectionService service;

	@PostMapping("/approve")
	public Mono<Connection> approve(@RequestBody @Valid ApproveConnectionRequest request,
			@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
		return service.approveConnection(request, authHeader);
	}

	/*
	 * @PostMapping public Mono<ConnectionResponse> create(@RequestBody @Valid
	 * ConnectionRequest request, ServerWebExchange exchange) { String authHeader =
	 * exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
	 * return service.createConnection(request, authHeader); }
	 */

	@GetMapping
	public Flux<ConnectionResponse> all() {
		return service.getAllConnections();
	}

	@GetMapping("/consumer/{consumerId}")
	public Flux<ConnectionResponse> byConsumer(@PathVariable String consumerId,
			@RequestParam(required = false) UtilityType utilityType) {
		return service.getConnectionsByConsumer(consumerId, utilityType);
	}

	@GetMapping("/{connectionId}")
	public Mono<ConnectionResponse> byId(@PathVariable String connectionId) {
		return service.getConnectionById(connectionId);
	}
}