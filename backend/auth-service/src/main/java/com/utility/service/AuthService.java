package com.utility.service;

import com.utility.dto.LoginRequest;
import com.utility.dto.LoginResponse;
import com.utility.dto.RegisterRequest;
import com.utility.model.User;

import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<LoginResponse> login(LoginRequest request);
    Mono<User> register(RegisterRequest request);
}
