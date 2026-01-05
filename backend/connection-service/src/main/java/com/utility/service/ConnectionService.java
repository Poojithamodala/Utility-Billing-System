package com.utility.service;

import com.utility.dto.ApproveConnectionRequest;
import com.utility.dto.ConnectionRequest;
import com.utility.dto.ConnectionResponse;
import com.utility.model.Connection;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ConnectionService {
	Mono<Connection> approveConnection(ApproveConnectionRequest request, String authHeader);
    Mono<ConnectionResponse> createConnection(ConnectionRequest request, String authHeader);
    Flux<ConnectionResponse> getConnectionsByConsumer(String consumerId);
    Flux<ConnectionResponse> getAllConnections();
    Mono<ConnectionResponse> getConnectionById(String connectionId);
}