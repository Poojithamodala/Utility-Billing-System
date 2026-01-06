package com.utility.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AuthClientConfig {

    @Bean
    public WebClient authWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:8765/auth-service") 
                .build();
    }
}