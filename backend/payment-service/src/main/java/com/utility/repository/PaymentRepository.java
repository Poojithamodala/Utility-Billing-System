package com.utility.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.utility.model.Payment;

import reactor.core.publisher.Flux;

@Repository
public interface PaymentRepository extends ReactiveMongoRepository<Payment, String> {
	Flux<Payment> findByBillId(String billId);
	Flux<Payment> findByConsumerId(String consumerId);
}
