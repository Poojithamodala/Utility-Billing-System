package com.utility.service;

import com.utility.dto.ActivateRequest;
import com.utility.dto.ConsumerAuthCreateRequest;
import com.utility.dto.LoginRequest;
import com.utility.dto.LoginResponse;
import com.utility.dto.RegisterRequest;

import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<Void> register(RegisterRequest request);
    Mono<LoginResponse> login(LoginRequest request);
    Mono<Void> createConsumerUser(ConsumerAuthCreateRequest request);
    Mono<Void> activateConsumer(ActivateRequest request);
}