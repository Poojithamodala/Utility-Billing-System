package com.utility.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ConsumerClient {

    private final WebClient consumerWebClient;

    public Mono<Void> validateConsumerExists(String consumerId, String authHeader) {

        return consumerWebClient.get()
            .uri("/consumers/{id}", consumerId)
            .header(HttpHeaders.AUTHORIZATION, authHeader)
            .exchangeToMono(response -> {

                if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                    return Mono.error(new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Consumer not found"
                    ));
                }

                if (response.statusCode().is4xxClientError()) {
                    return Mono.error(new ResponseStatusException(
                            HttpStatus.FORBIDDEN,
                            "Not authorized to access consumer"
                    ));
                }

                if (response.statusCode().is5xxServerError()) {
                    return Mono.error(new ResponseStatusException(
                            HttpStatus.SERVICE_UNAVAILABLE,
                            "Consumer service unavailable"
                    ));
                }

                return Mono.empty(); // consumer exists
            });
    }
}