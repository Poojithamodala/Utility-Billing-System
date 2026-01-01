package com.utility.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.utility.model.TariffPlan;
import com.utility.model.UtilityType;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TariffRepository extends ReactiveMongoRepository<TariffPlan, String> {
	Flux<TariffPlan> findByUtilityType(UtilityType utilityType);

	Mono<Boolean> existsByUtilityTypeAndName(UtilityType utilityType, String name);

}