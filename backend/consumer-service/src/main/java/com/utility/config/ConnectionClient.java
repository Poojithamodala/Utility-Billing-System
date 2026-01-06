package com.utility.config;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.utility.dto.ConnectionResponse;
import com.utility.dto.UtilityType;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ConnectionClient {

    private final WebClient connectionWebClient;

    public Mono<Boolean> hasActiveConnection(
            String consumerId,
            UtilityType utilityType
    ) {
        return connectionWebClient
            .get()
            .uri(uriBuilder -> uriBuilder
                .path("/connections/consumer/{consumerId}")
                .queryParam("utilityType", utilityType)
                .build(consumerId)
            )
            .retrieve()
            .bodyToFlux(ConnectionResponse.class)
            .hasElements();
    }
}