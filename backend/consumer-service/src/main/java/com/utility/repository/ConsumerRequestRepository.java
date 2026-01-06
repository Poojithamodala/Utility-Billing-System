package com.utility.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.utility.dto.RequestStatus;
import com.utility.model.Consumer;
import com.utility.model.ConsumerRegistrationRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ConsumerRequestRepository extends ReactiveMongoRepository<ConsumerRegistrationRequest, String> {
	Mono<Boolean> existsByEmail(String email);
	Mono<ConsumerRegistrationRequest> findByEmail(String email);
	Flux<ConsumerRegistrationRequest> findByStatus(RequestStatus status);
}
