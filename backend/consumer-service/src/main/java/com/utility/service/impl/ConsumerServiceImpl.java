package com.utility.service.impl;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.utility.config.AuthClient;
import com.utility.dto.ConsumerRequest;
import com.utility.dto.ConsumerResponse;
import com.utility.model.Consumer;
import com.utility.repository.ConsumerRepository;
import com.utility.service.ConsumerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsumerServiceImpl implements ConsumerService {

	private final ConsumerRepository repository;
    private final AuthClient authClient;

    @Override
    public Mono<ConsumerResponse> createConsumer(
            String authHeader,
            ConsumerRequest request) {

        if (authHeader == null || authHeader.isBlank()) {
            return Mono.error(new RuntimeException("Missing Authorization header"));
        }

        request.setName(request.getName().trim());
        request.setEmail(request.getEmail().trim().toLowerCase());
        request.setPhone(request.getPhone().trim());
        request.setAddress(request.getAddress().trim());

        return repository.existsByEmail(request.getEmail())
                .flatMap(emailExists -> {
                    if (emailExists) {
                        return Mono.error(
                                new RuntimeException("Consumer email already exists"));
                    }

                    Consumer consumer = Consumer.builder()
                            .username(request.getEmail())   // username = email
                            .name(request.getName())
                            .email(request.getEmail())
                            .phone(request.getPhone())
                            .address(request.getAddress())
                            .build();

                    return repository.save(consumer);
                })
                .flatMap(savedConsumer ->
                        authClient.createConsumerUser(
                                savedConsumer.getEmail(),
                                authHeader        
                        ).thenReturn(savedConsumer)
                )
                .map(this::mapToResponse);
    }

    @Override
    public Mono<ConsumerResponse> getConsumerById(String id) {
    	if (id == null || id.isBlank()) {
            return Mono.error(new RuntimeException("Consumer ID is required"));
        }
    	if (!id.matches("^[a-fA-F0-9]{24}$")) {
            return Mono.error(new RuntimeException("Invalid consumer ID format"));
        }
    	return repository.findById(id)
    	        .switchIfEmpty(Mono.error(
    	            new ResponseStatusException(
    	                HttpStatus.NOT_FOUND,
    	                "Consumer not found"
    	            )
    	        ))
    	        .map(this::mapToResponse);
    }

    @Override
    public Mono<ConsumerResponse> getMyProfile(String username) {
    	if (username == null || username.isBlank()) {
            return Mono.error(new RuntimeException("Invalid authentication token"));
        }
        return repository.findByUsername(username)
                .switchIfEmpty(Mono.error(new RuntimeException("Consumer not found")))
                .map(this::mapToResponse);
    }
    
    @Override
    public Flux<ConsumerResponse> getAllConsumers() {
    	return repository.findAll()
                .switchIfEmpty(Mono.error(
                        new RuntimeException("No consumers found")
                ))
                .map(this::mapToResponse);
    }
    
    @Override
    public Mono<ConsumerResponse> updateConsumer(String id, ConsumerRequest dto) {

        if (id == null || id.isBlank()) {
            return Mono.error(new IllegalArgumentException("Consumer ID is required"));
        }

        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Consumer not found")))
                .flatMap(existing -> {

                    // Email duplication check only if email is being updated
                    if (dto.getEmail() != null &&
                            !dto.getEmail().isBlank() &&
                            !dto.getEmail().equals(existing.getEmail())) {

                        return repository.existsByEmail(dto.getEmail())
                                .flatMap(exists -> {
                                    if (exists) {
                                        return Mono.error(
                                                new IllegalStateException("Email already in use"));
                                    }

                                    updateFieldsIfPresent(existing, dto);
                                    return repository.save(existing);
                                });
                    }

                    updateFieldsIfPresent(existing, dto);
                    return repository.save(existing);
                })
                .map(this::mapToResponse);
    }

    private void updateFieldsIfPresent(Consumer consumer, ConsumerRequest dto) {
        if (dto.getName() != null && !dto.getName().isBlank()) {
            consumer.setName(dto.getName());
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            consumer.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null && !dto.getPhone().isBlank()) {
            consumer.setPhone(dto.getPhone());
        }
        if (dto.getAddress() != null && !dto.getAddress().isBlank()) {
            consumer.setAddress(dto.getAddress());
        }
    }
    
    @Override
    public Mono<Void> deleteConsumer(String id) {

    	if (id == null || id.isBlank()) {
            return Mono.error(new RuntimeException("Consumer ID is required"));
        }
    	return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Consumer not found or already deleted")))
                .flatMap(consumer -> repository.delete(consumer));
    }
    
    private ConsumerResponse mapToResponse(Consumer consumer) {
        return ConsumerResponse.builder()
                .id(consumer.getId())
                .name(consumer.getName())
                .username(consumer.getUsername())
                .email(consumer.getEmail())
                .phone(consumer.getPhone())
                .address(consumer.getAddress())
                .build();
    }
}