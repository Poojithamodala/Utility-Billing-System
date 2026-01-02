package com.utility.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.utility.dto.ActivateRequest;
import com.utility.dto.ConsumerAuthCreateRequest;
import com.utility.dto.LoginRequest;
import com.utility.dto.LoginResponse;
import com.utility.dto.RegisterRequest;
import com.utility.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {

		return authService.register(request).thenReturn(Map.of("message", "User registered successfully"));
	}

	@PostMapping("/login")
	public Mono<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

		return authService.login(request);
	}

	@PostMapping("/internal/create-consumer")
	public Mono<Void> createConsumerUser(@RequestBody ConsumerAuthCreateRequest request) {

		return authService.createConsumerUser(request);
	}

	@PostMapping("/activate")
	public Mono<Map<String, String>> activate(@Valid @RequestBody ActivateRequest request) {

		return authService.activateConsumer(request).thenReturn(Map.of("message", "Account activated successfully"));
	}
}
