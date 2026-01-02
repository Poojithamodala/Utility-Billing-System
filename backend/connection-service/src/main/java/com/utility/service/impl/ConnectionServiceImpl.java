package com.utility.service.impl;


import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.utility.config.ConsumerClient;
import com.utility.dto.ConnectionRequest;
import com.utility.dto.ConnectionResponse;
import com.utility.model.Connection;
import com.utility.model.ConnectionStatus;
import com.utility.repository.ConnectionRepository;
import com.utility.repository.TariffRepository;
import com.utility.service.ConnectionService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ConnectionServiceImpl implements ConnectionService {

	private final ConnectionRepository repository;
	private final TariffRepository tariffRepository;
	private final ConsumerClient consumerClient;

	@Override
	public Mono<ConnectionResponse> createConnection(ConnectionRequest request, String authHeader) {

		return consumerClient.validateConsumerExists(request.getConsumerId(), authHeader)

				//Tariff validation
				.then(tariffRepository.findById(request.getTariffPlanId()).switchIfEmpty(
						Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Tariff plan not found"))))
				.flatMap(tariff -> {
					if (!tariff.getUtilityType().equals(request.getUtilityType())) {
						return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
								"Tariff plan utility type does not match connection utility type"));
					}
					return Mono.just(request);
				})

				//Meter uniqueness
				.flatMap(req -> repository.existsByUtilityTypeAndMeterNumber(req.getUtilityType(), req.getMeterNumber())
						.flatMap(meterExists -> {
							if (meterExists) {
								return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT,
										"Meter already registered for this utility"));
							}
							return Mono.just(req);
						}))

				//One ACTIVE connection per utility
				.flatMap(req -> repository.existsByConsumerIdAndUtilityTypeAndStatus(req.getConsumerId(),
						req.getUtilityType(), ConnectionStatus.ACTIVE).flatMap(activeExists -> {
							if (activeExists) {
								return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT,
										"Active connection already exists for this utility"));
							}
							return Mono.just(req);
						}))

				//Save
				.flatMap(req ->
			    consumerClient.getConsumerById(req.getConsumerId(), authHeader)
			        .map(consumer -> Connection.builder()
			            .consumerId(req.getConsumerId())
			            .consumerEmail(consumer.getEmail()) 
			            .utilityType(req.getUtilityType())
			            .meterNumber(req.getMeterNumber())
			            .tariffPlanId(req.getTariffPlanId())
			            .billingCycle(req.getBillingCycle())
			            .status(ConnectionStatus.ACTIVE)
			            .connectionDate(LocalDate.now())
			            .build()
			        )
			        .flatMap(repository::save)
			)
			.map(this::toResponse);
	}

	@Override
	public Flux<ConnectionResponse> getConnectionsByConsumer(String consumerId) {

		//Validate consumer exists (has at least one connection ever)
		return repository.findByConsumerId(consumerId)
				.switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
						"No connections found for the given consumer")))

				//Return only ACTIVE connections
				.filter(connection -> connection.getStatus() == ConnectionStatus.ACTIVE)

				//Handle case where consumer has only inactive connections
				.switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
						"No active connections found for the given consumer")))

				//Map to response
				.map(this::toResponse);
	}

	@Override
	public Flux<ConnectionResponse> getAllConnections() {

		return repository.findAll()

				//Filter only ACTIVE connections
				.filter(connection -> connection.getStatus() == ConnectionStatus.ACTIVE)

				//If DB has no active connections
				.switchIfEmpty(
						Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No active connections found")))

				//Map to response
				.map(this::toResponse);
	}

	@Override
	public Mono<ConnectionResponse> getConnectionById(String connectionId) {
		return repository.findById(connectionId)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Connection not found")))
				.map(this::toResponse);
	}

	private ConnectionResponse toResponse(Connection c) {
		return ConnectionResponse.builder()
				.id(c.getId())
				.consumerId(c.getConsumerId())
				.consumerEmail(c.getConsumerEmail())
				.utilityType(c.getUtilityType())
				.meterNumber(c.getMeterNumber())
				.tariffPlanId(c.getTariffPlanId())
				.billingCycle(c.getBillingCycle())
				.status(c.getStatus())
				.connectionDate(c.getConnectionDate())
				.build();
	}
}