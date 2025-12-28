package com.utility.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utility.dto.LoginRequest;
import com.utility.dto.LoginResponse;
import com.utility.dto.RegisterRequest;
import com.utility.model.User;
import com.utility.service.AuthService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Mono<LoginResponse> login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public Mono<User> register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }
}
