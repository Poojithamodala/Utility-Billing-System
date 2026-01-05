package com.utility.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.utility.dto.RequestStatus;
import com.utility.dto.UtilityType;
import com.utility.model.ConnectionRequestEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ConnectionRequestRepository extends ReactiveMongoRepository<ConnectionRequestEntity, String> {
	Mono<Boolean> existsByConsumerIdAndUtilityTypeAndStatus(String consumerId, UtilityType utilityType, RequestStatus status);
	Flux<ConnectionRequestEntity> findByStatus(RequestStatus status);
}