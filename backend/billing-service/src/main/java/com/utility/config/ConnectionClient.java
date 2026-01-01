package com.utility.config;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.utility.dto.ConnectionDTO;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ConnectionClient {

    private final WebClient connectionWebClient;

    public Mono<ConnectionDTO> getConnection(String id, String authHeader) {
        return connectionWebClient.get()
                .uri("/connections/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .bodyToMono(ConnectionDTO.class);
    }
}
