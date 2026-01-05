package com.utility.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ConnectionClient {

    private final WebClient connectionWebClient;

    public Mono<ConnectionDTO> getConnection(
            String connectionId,
            String authHeader) {

        return connectionWebClient.get()
                .uri("/connections/{id}", connectionId)
                .header(HttpHeaders.AUTHORIZATION, authHeader) 
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        res -> Mono.error(new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED, "Unauthorized to access Connection Service"))
                )
                .bodyToMono(ConnectionDTO.class);
    }
    
    public Flux<ConnectionDTO> getActiveConnections(String authHeader) {
        return connectionWebClient.get()
                .uri("/connections")
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .bodyToFlux(ConnectionDTO.class)
                .filter(conn -> conn.getStatus() == ConnectionStatus.ACTIVE);
    }
}