package com.utility.config;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.utility.dto.TariffPlanDTO;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TariffClient {

    private final WebClient tariffWebClient;

    public Mono<TariffPlanDTO> getTariff(String tariffPlanId, String authHeader) {
        return tariffWebClient.get()
                .uri("/tariffs/{id}", tariffPlanId)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .bodyToMono(TariffPlanDTO.class);
    }
}