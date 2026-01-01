package com.utility.config;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.utility.dto.MeterReadingDTO;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MeterReadingClient {

	private final WebClient meterReadingWebClient;

	public Mono<MeterReadingDTO> getReading(String readingId, String authHeader) {
		return meterReadingWebClient.get()
				.uri("/meter-readings/{id}", readingId)
				.header(HttpHeaders.AUTHORIZATION, authHeader)
				.retrieve()
				.bodyToMono(MeterReadingDTO.class);
	}
}