package com.utility.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient connectionWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:8083") // connection-service
                .build();
    }
    
    @Bean
    public WebClient tariffWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:8083") // connection-service
                .build();
    }

    @Bean
    public WebClient meterReadingWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:8084") // meter-reading-service
                .build();
    }
}
