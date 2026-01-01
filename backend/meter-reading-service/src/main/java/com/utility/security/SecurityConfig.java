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

		return http.csrf(ServerHttpSecurity.CsrfSpec::disable).httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
				.formLogin(ServerHttpSecurity.FormLoginSpec::disable)

				.securityContextRepository(NoOpServerSecurityContextRepository.getInstance())

				.authorizeExchange(exchange -> exchange
						.pathMatchers(HttpMethod.POST, "/meter-readings").hasRole("BILLING_OFFICER")
						.pathMatchers(HttpMethod.GET, "/meter-readings").hasRole("ADMIN")
						.pathMatchers(HttpMethod.GET, "/meter-readings/connection/**").hasAnyRole("ADMIN", "BILLING_OFFICER", "CONSUMER")
						.pathMatchers(HttpMethod.GET, "/meter-readings/*").hasAnyRole("ADMIN", "BILLING_OFFICER")
						.anyExchange().permitAll()
				)
				.addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
				.build();
	}
}