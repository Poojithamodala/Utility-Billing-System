package com.utility.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain security(ServerHttpSecurity http) {

        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)

            // Auth Service does NOT validate JWT
            .authorizeExchange(ex -> ex
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Public endpoints
                .pathMatchers(
                    "/auth/login",
                    "/auth/register",
                    "/auth/activate"
                ).permitAll()

                // Internal endpoint (Gateway already enforces ADMIN)
                .pathMatchers("/auth/internal/**").permitAll()

                // Everything else is allowed (no resource protection here)
                .anyExchange().permitAll()
            )
            .build();
    }
}