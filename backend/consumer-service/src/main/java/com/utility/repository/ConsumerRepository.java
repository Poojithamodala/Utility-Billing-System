package com.utility.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.utility.model.Consumer;

import reactor.core.publisher.Mono;

@Repository
public interface ConsumerRepository extends ReactiveMongoRepository<Consumer, String> {
	Mono<Consumer> findByUsername(String username);
	Mono<Boolean> existsByEmail(String email);
	Mono<Consumer> findByEmail(String email);
}