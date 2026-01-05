package com.utility.repository;

import java.time.LocalDate;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.utility.model.MeterReading;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MeterReadingRepository extends ReactiveMongoRepository<MeterReading, String> {
	Mono<MeterReading> findTopByConnectionIdOrderByReadingDateDesc(String connectionId);
	Flux<MeterReading> findByConnectionId(String connectionId);
	Mono<Boolean> existsByConnectionIdAndReadingDateBetween(String connectionId, LocalDate start, LocalDate end);
}
