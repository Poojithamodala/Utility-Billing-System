package com.utility.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ConsumerClientConfig {
	
	@Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient consumerWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:8082") // consumer-service
                .build();
    }
}

