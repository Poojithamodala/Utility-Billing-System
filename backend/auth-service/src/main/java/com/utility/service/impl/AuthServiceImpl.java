package com.utility.service.impl;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.utility.dto.LoginRequest;
import com.utility.dto.LoginResponse;
import com.utility.dto.RegisterRequest;
import com.utility.model.User;
import com.utility.repository.UserRepository;
import com.utility.security.JwtUtil;
import com.utility.service.AuthService;

import reactor.core.publisher.Mono;

@Service
public class AuthServiceImpl implements AuthService {
	private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
	public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
	}

    public Mono<LoginResponse> login(LoginRequest request) {
        return userRepository.findByUsername(request.getUsername())
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid credentials")))
                .map(user -> new LoginResponse(
                        jwtUtil.generateToken(user),
                        user.getRole(),
                        user.getUsername()
                ));
    }

    public Mono<User> register(RegisterRequest request) {
        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        return userRepository.save(user);
    }
}
