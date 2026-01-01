package com.utility.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthClient {

    private final WebClient authWebClient;

    public Mono<Void> createConsumerUser(String email, String authHeader) {

        return authWebClient.post()
                .uri("/auth/internal/create-consumer")
                .header(HttpHeaders.AUTHORIZATION, authHeader) 
                .bodyValue(new ConsumerAuthCreateRequest(email))
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        response -> Mono.error(
                                new RuntimeException("Failed to create auth consumer user")
                        ))
                .bodyToMono(Void.class);
    }
}