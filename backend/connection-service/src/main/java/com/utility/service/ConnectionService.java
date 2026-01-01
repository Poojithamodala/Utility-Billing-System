package com.utility.service;

import com.utility.dto.ConnectionRequest;
import com.utility.dto.ConnectionResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ConnectionService {
    Mono<ConnectionResponse> createConnection(ConnectionRequest request, String authHeader);
    Flux<ConnectionResponse> getConnectionsByConsumer(String consumerId);
    Flux<ConnectionResponse> getAllConnections();
    Mono<ConnectionResponse> getConnectionById(String connectionId);
}