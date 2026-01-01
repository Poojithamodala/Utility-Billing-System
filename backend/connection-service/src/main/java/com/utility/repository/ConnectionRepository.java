package com.utility.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.utility.model.Connection;
import com.utility.model.ConnectionStatus;
import com.utility.model.UtilityType;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ConnectionRepository extends ReactiveMongoRepository<Connection, String> {
	Flux<Connection> findByConsumerId(String consumerId);

	// Check if a meter already exists for a utility
	Mono<Boolean> existsByUtilityTypeAndMeterNumber(UtilityType utilityType, String meterNumber);

	// Check if consumer already has an ACTIVE connection for the same utility
	Mono<Boolean> existsByConsumerIdAndUtilityTypeAndStatus(String consumerId, UtilityType utilityType,
			ConnectionStatus status);

	Mono<Connection> findByConsumerIdAndUtilityTypeAndStatus(String consumerId, UtilityType utilityType,
			ConnectionStatus status);
}
