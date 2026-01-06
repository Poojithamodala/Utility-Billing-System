package com.utility.repository;

import java.time.LocalDate;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.utility.model.Bill;
import com.utility.model.BillStatus;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BillRepository extends ReactiveMongoRepository<Bill, String> {
	Flux<Bill> findByConsumerId(String consumerId);
	Flux<Bill> findByStatus(BillStatus status);
	Mono<Boolean> existsByMeterReadingId(String meterReadingId);
	Flux<Bill> findByStatusNotAndDueDateBefore(BillStatus status, LocalDate date);
	Flux<Bill> findByOutstandingAmountGreaterThan(double amount);
}