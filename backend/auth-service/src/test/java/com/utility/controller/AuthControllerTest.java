package com.utility.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.utility.dto.ActivateRequest;
import com.utility.dto.ConsumerAuthCreateRequest;
import com.utility.dto.LoginRequest;
import com.utility.dto.LoginResponse;
import com.utility.dto.RegisterRequest;
import com.utility.model.Role;
import com.utility.service.AuthService;

import reactor.core.publisher.Mono;

class AuthControllerTest {

    private WebTestClient webTestClient;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = Mockito.mock(AuthService.class);
        AuthController authController = new AuthController(authService);

        webTestClient = WebTestClient
                .bindToController(authController)
                .build();
    }

    @Test
    void register_shouldReturnCreated() {
        RegisterRequest request = new RegisterRequest(
                "testuser",
                "test@example.com",
                "Password@123",
                Role.CONSUMER
        );

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("User registered successfully");
    }

    @Test
    void login_shouldReturnLoginResponse() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Password@123");

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(Mono.just(new LoginResponse()));

        webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void createConsumerUser_shouldReturnOk() {
        ConsumerAuthCreateRequest request = new ConsumerAuthCreateRequest();
        request.setEmail("consumer@example.com");

        when(authService.createConsumerUser(any(ConsumerAuthCreateRequest.class)))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/auth/internal/create-consumer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void activate_shouldReturnSuccessMessage() {
        ActivateRequest request = new ActivateRequest();
        request.setEmail("test@example.com");
        request.setPassword("Password@123");

        when(authService.activateConsumer(any(ActivateRequest.class)))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/auth/activate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Account activated successfully");
    }
}
