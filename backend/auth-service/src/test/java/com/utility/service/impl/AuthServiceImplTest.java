package com.utility.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.utility.dto.ActivateRequest;
import com.utility.dto.ConsumerAuthCreateRequest;
import com.utility.dto.LoginRequest;
import com.utility.dto.RegisterRequest;
import com.utility.model.Role;
import com.utility.model.User;
import com.utility.repository.UserRepository;
import com.utility.security.JwtUtil;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_consumerRole_shouldFail() {
        RegisterRequest request =
                new RegisterRequest("u", "e@e.com", "Password@123", Role.CONSUMER);

        StepVerifier.create(authService.register(request))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    void register_usernameExists_shouldFail() {
        RegisterRequest request =
                new RegisterRequest("u", "e@e.com", "Password@123", Role.ADMIN);

        when(userRepository.existsByUsername("u"))
                .thenReturn(Mono.just(true));

        StepVerifier.create(authService.register(request))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void register_emailExists_shouldFail() {
        RegisterRequest request =
                new RegisterRequest("u", "e@e.com", "Password@123", Role.ADMIN);

        when(userRepository.existsByUsername("u"))
                .thenReturn(Mono.just(false));
        when(userRepository.existsByEmail("e@e.com"))
                .thenReturn(Mono.just(true));

        StepVerifier.create(authService.register(request))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void register_weakPassword_shouldFail() {
        RegisterRequest request =
                new RegisterRequest("u", "e@e.com", "password", Role.ADMIN);

        when(userRepository.existsByUsername(any()))
                .thenReturn(Mono.just(false));
        when(userRepository.existsByEmail(any()))
                .thenReturn(Mono.just(false));

        StepVerifier.create(authService.register(request))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    void register_success() {
        RegisterRequest request =
                new RegisterRequest("u", "e@e.com", "Password@123", Role.ADMIN);

        when(userRepository.existsByUsername(any()))
                .thenReturn(Mono.just(false));
        when(userRepository.existsByEmail(any()))
                .thenReturn(Mono.just(false));
        when(passwordEncoder.encode(any()))
                .thenReturn("ENCODED");
        when(userRepository.save(any()))
                .thenReturn(Mono.just(new User()));

        StepVerifier.create(authService.register(request))
                .verifyComplete();
    }

    @Test
    void login_userNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("u");
        request.setPassword("p");

        when(userRepository.findByUsername("u"))
                .thenReturn(Mono.empty());

        StepVerifier.create(authService.login(request))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    void login_consumerNotActivated() {
        User user = User.builder()
                .role(Role.CONSUMER)
                .enabled(false)
                .failedAttempts(0)
                .build();

        when(userRepository.findByUsername("u"))
                .thenReturn(Mono.just(user));

        LoginRequest request = new LoginRequest();
        request.setUsername("u");
        request.setPassword("p");

        StepVerifier.create(authService.login(request))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    void login_accountLocked() {
        User user = User.builder()
                .role(Role.ADMIN)
                .enabled(true)
                .failedAttempts(5)
                .build();

        when(userRepository.findByUsername("u"))
                .thenReturn(Mono.just(user));

        LoginRequest request = new LoginRequest();
        request.setUsername("u");
        request.setPassword("p");

        StepVerifier.create(authService.login(request))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    void login_wrongPassword() {
        User user = User.builder()
                .role(Role.ADMIN)
                .enabled(true)
                .failedAttempts(0)
                .password("ENC")
                .build();

        when(userRepository.findByUsername("u"))
                .thenReturn(Mono.just(user));
        when(passwordEncoder.matches(any(), any()))
                .thenReturn(false);
        when(userRepository.save(any()))
                .thenReturn(Mono.just(user));

        LoginRequest request = new LoginRequest();
        request.setUsername("u");
        request.setPassword("wrong");

        StepVerifier.create(authService.login(request))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    void login_success() {
        User user = User.builder()
                .username("u")
                .role(Role.ADMIN)
                .enabled(true)
                .failedAttempts(0)
                .password("ENC")
                .build();

        when(userRepository.findByUsername("u"))
                .thenReturn(Mono.just(user));
        when(passwordEncoder.matches(any(), any()))
                .thenReturn(true);
        when(userRepository.save(any()))
                .thenReturn(Mono.just(user));
        when(jwtUtil.generateToken(any(), any()))
                .thenReturn("TOKEN");

        LoginRequest request = new LoginRequest();
        request.setUsername("u");
        request.setPassword("Password@123");

        StepVerifier.create(authService.login(request))
                .expectNextMatches(res -> res.getToken().equals("TOKEN"))
                .verifyComplete();
    }

    @Test
    void createConsumerUser_emailExists() {
        when(userRepository.existsByEmail(any()))
                .thenReturn(Mono.just(true));

        StepVerifier.create(authService.createConsumerUser(
                new ConsumerAuthCreateRequest()))
                .verifyComplete();
    }

    @Test
    void createConsumerUser_success() {
        when(userRepository.existsByEmail(any()))
                .thenReturn(Mono.just(false));
        when(userRepository.save(any()))
                .thenReturn(Mono.just(new User()));

        StepVerifier.create(authService.createConsumerUser(
                new ConsumerAuthCreateRequest()))
                .verifyComplete();
    }

    @Test
    void activate_invalidRequest() {
        StepVerifier.create(authService.activateConsumer(null))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    void activate_userNotFound() {
        ActivateRequest request = new ActivateRequest();
        request.setEmail("e@e.com");
        request.setPassword("Password@123");

        when(userRepository.findByUsername(any()))
                .thenReturn(Mono.empty());

        StepVerifier.create(authService.activateConsumer(request))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    void activate_success() {
        User user = User.builder()
                .role(Role.CONSUMER)
                .enabled(false)
                .password(null)
                .build();

        ActivateRequest request = new ActivateRequest();
        request.setEmail("e@e.com");
        request.setPassword("Password@123");

        when(userRepository.findByUsername(any()))
                .thenReturn(Mono.just(user));
        when(passwordEncoder.encode(any()))
                .thenReturn("ENC");
        when(userRepository.save(any()))
                .thenReturn(Mono.just(user));

        StepVerifier.create(authService.activateConsumer(request))
                .verifyComplete();
    }
}