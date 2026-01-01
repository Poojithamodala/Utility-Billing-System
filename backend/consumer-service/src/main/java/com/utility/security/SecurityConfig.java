package com.utility.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {

        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

            .securityContextRepository(
                NoOpServerSecurityContextRepository.getInstance()
            )

            .authorizeExchange(exchange -> exchange
                .pathMatchers(HttpMethod.POST, "/consumers").hasRole("ADMIN")
                .pathMatchers(HttpMethod.PUT, "/consumers/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/consumers/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.GET, "/consumers").hasRole("ADMIN")
                .pathMatchers(HttpMethod.GET, "/consumers/**").hasRole("ADMIN")
                .pathMatchers("/consumers/me").authenticated()
                .anyExchange().permitAll()
            )

            .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
    }
}