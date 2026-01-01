package com.utility.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.utility.dto.ActivateRequest;
import com.utility.dto.ConsumerAuthCreateRequest;
import com.utility.dto.LoginRequest;
import com.utility.dto.LoginResponse;
import com.utility.dto.RegisterRequest;
import com.utility.model.Role;
import com.utility.model.User;
import com.utility.repository.UserRepository;
import com.utility.security.JwtUtil;
import com.utility.service.AuthService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	@Override
	public Mono<Void> register(RegisterRequest request) {

		if (request.getRole() == Role.CONSUMER) {
			return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Consumer registration is not allowed"));
		}

		return userRepository.existsByUsername(request.getUsername()).flatMap(exists -> {
			if (exists) {
				return Mono.error(new RuntimeException("Username already exists"));
			}
			return userRepository.existsByEmail(request.getEmail());
		}).flatMap(emailExists -> {
			if (emailExists) {
				return Mono.error(new RuntimeException("Email already exists"));
			}

			List<String> weakPasswords = List.of("password", "12345678", "admin@123", "11111111", "87654321");

			if (weakPasswords.contains(request.getPassword().toLowerCase())) {
				return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is too weak"));
			}

			User user = User.builder().username(request.getUsername()).email(request.getEmail())
					.password(passwordEncoder.encode(request.getPassword())).role(request.getRole()).build();
			return userRepository.save(user)
					.doOnNext(savedUser -> System.out.println("User registered successfully: " + savedUser.getUsername()))
					.then();
		});
	}

	@Override
	public Mono<LoginResponse> login(LoginRequest request) {

		String username = request.getUsername().trim();
		String password = request.getPassword();

		if (username.equalsIgnoreCase("admin") && password.equalsIgnoreCase("admin")) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid credentials"));
		}
		return userRepository.findByUsername(request.getUsername())
				.switchIfEmpty(Mono
						.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password")))
				.flatMap(user -> {
					if (user.getRole() == Role.CONSUMER && !user.isEnabled()) {
						return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Account not activated"));
					}

					if (user.getFailedAttempts() >= 5) {
						return Mono.error(new ResponseStatusException(HttpStatus.LOCKED,
								"Account locked due to multiple failed attempts"));
					}
					// Password mismatch
					if (!passwordEncoder.matches(password, user.getPassword())) {
						user.setFailedAttempts(user.getFailedAttempts() + 1);
						return Mono.error(
								new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));
					}
					user.setFailedAttempts(0);
					String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
					return Mono.just(LoginResponse.builder().token(token).role(user.getRole().name()).build());
				});
	}

	@Override
	public Mono<Void> createConsumerUser(ConsumerAuthCreateRequest request) {

		return userRepository.existsByEmail(request.getEmail()).flatMap(exists -> {
			if (exists) {
				return Mono.empty();
			}
			User user = User.builder()
					.username(request.getEmail())
					.email(request.getEmail())
					.role(Role.CONSUMER)
					.enabled(false)
					.failedAttempts(0)
					.build();
			return userRepository.save(user).then();
		});
	}

	@Override
	public Mono<Void> activateConsumer(ActivateRequest request) {

		if (request == null || request.getEmail() == null || request.getEmail().isBlank()
				|| request.getPassword() == null || request.getPassword().isBlank()) {

			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email and password are required"));
		}

		String email = request.getEmail().trim().toLowerCase();

		return userRepository.findByUsername(email)
				.switchIfEmpty(
						Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Consumer account not found")))
				.flatMap(user -> {
					if (user.getRole() != Role.CONSUMER) {
						return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Only consumer accounts can be activated"));
					}

					if (user.isEnabled()) {
						return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account already activated"));
					}

					if (user.getPassword() != null) {
						return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "Password already set"));
					}

					if (request.getPassword().length() < 8) {
						return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 8 characters"));
					}
					user.setPassword(passwordEncoder.encode(request.getPassword()));
					user.setEnabled(true);
					user.setFailedAttempts(0);
					return userRepository.save(user).then();
				});
	}
}