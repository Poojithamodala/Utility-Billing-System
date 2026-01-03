package com.utility.service;

import com.utility.dto.ConsumerRegistrationRequestResponse;
import com.utility.dto.ConsumerRequest;
import com.utility.dto.ConsumerResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ConsumerService {
	Mono<Void> submitRegistrationRequest(ConsumerRequest request);
	Flux<ConsumerRegistrationRequestResponse> getAllRegistrationRequests();
	Mono<Void> approveRequest(String requestId, String authHeader);
	Mono<Void> rejectRequest(String requestId, String reason);
	Mono<ConsumerResponse> createConsumer(String authHeader, ConsumerRequest request);
	Flux<ConsumerResponse> getAllConsumers();
	Mono<ConsumerResponse> getConsumerById(String id);
	
	// View own profile (Consumer)
	Mono<ConsumerResponse> getMyProfile(String username);
	Mono<ConsumerResponse> updateConsumer(String id, ConsumerRequest dto);
	Mono<Void> deleteConsumer(String id);
}
