package com.utility.security;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import reactor.core.publisher.Flux;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)

            .authorizeExchange(ex -> ex
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Auth Service
                .pathMatchers("/auth-service/auth/internal/**").hasRole("ADMIN")
                .pathMatchers("/auth-service/auth/**").permitAll()
                

                // Consumer Service
                .pathMatchers(HttpMethod.POST, "/consumer-service/consumers/request").permitAll()
                .pathMatchers(HttpMethod.GET, "/consumer-service/consumers/requests").hasRole("ADMIN")
                .pathMatchers(HttpMethod.POST, "/consumer-service/consumers/requests/{id}/approve").hasRole("ADMIN")
                .pathMatchers(HttpMethod.POST, "/consumer-service/consumers/requests/{id}/reject").hasRole("ADMIN")
                .pathMatchers(HttpMethod.POST, "/consumer-service/consumers/request-connection").hasRole("CONSUMER")
                .pathMatchers("/consumer-service/consumers/profile").authenticated()
                .pathMatchers(HttpMethod.POST, "/consumer-service/consumers").hasRole("ADMIN")
                .pathMatchers(HttpMethod.PUT, "/consumer-service/consumers/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/consumer-service/consumers/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.GET, "/consumer-service/consumers/**").hasAnyRole("ADMIN", "CONSUMER")
                
                //connection service
                .pathMatchers(HttpMethod.POST, "/connection-service/connections/approve").hasRole("ADMIN")
                .pathMatchers(HttpMethod.POST, "/connection-service/connections").hasRole("ADMIN")
                .pathMatchers(HttpMethod.GET, "/connection-service/connections").hasAnyRole("ADMIN", "BILLING_OFFICER")
                .pathMatchers(HttpMethod.GET, "/connection-service/connections/consumer/**").hasAnyRole("ADMIN","CONSUMER")
                .pathMatchers(HttpMethod.GET, "/connection-service/connections/**").hasAnyRole("ADMIN", "CONSUMER", "BILLING_OFFICER")
                .pathMatchers(HttpMethod.POST, "/connection-service/tariffs").hasRole("ADMIN")
                .pathMatchers(HttpMethod.GET, "/connection-service/tariffs/utility/**").hasAnyRole("ADMIN", "BILLING_OFFICER", "CONSUMER")
                .pathMatchers(HttpMethod.GET, "/connection-service/tariffs/**").hasAnyRole("ADMIN", "BILLING_OFFICER", "CONSUMER")
                
                //meter reading service
                .pathMatchers(HttpMethod.POST, "/meter-reading-service/meter-readings").hasRole("BILLING_OFFICER")
				.pathMatchers(HttpMethod.GET, "/meter-reading-service/meter-readings").hasAnyRole("ADMIN", "BILLING_OFFICER")
				.pathMatchers(HttpMethod.GET, "/meter-reading-service/meter-readings/connection/**").hasAnyRole("ADMIN", "BILLING_OFFICER", "CONSUMER")
				.pathMatchers(HttpMethod.GET, "/meter-reading-service/meter-readings/*").hasAnyRole("ADMIN", "BILLING_OFFICER")
				
				//billing service
				.pathMatchers(HttpMethod.POST, "/billing-service/bills/generate").hasAnyRole("ADMIN", "BILLING_OFFICER")
				.pathMatchers(HttpMethod.GET, "/billing-service/bills/consumer/{consumerId}").hasAnyRole("ADMIN", "CONSUMER", "ACCOUNTS_OFFICER")
				
				//payment service
				.pathMatchers(HttpMethod.POST, "/payment-service/payments").hasAnyRole("ACCOUNTS_OFFICER", "CONSUMER")
				.pathMatchers(HttpMethod.GET, "/payment-service/payments/bill/**").hasAnyRole("ADMIN", "ACCOUNTS_OFFICER", "CONSUMER")
				.pathMatchers(HttpMethod.GET, "/payment-service/payments/consumer/**").hasAnyRole("ADMIN", "ACCOUNTS_OFFICER", "CONSUMER")

                .anyExchange().authenticated()
            )

            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt ->
                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            )
            .build();
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder(
            @Value("${spring.security.jwt.secret}") String secret) {

        SecretKey key = new SecretKeySpec(
            secret.getBytes(StandardCharsets.UTF_8),
            "HmacSHA256"
        );

        return NimbusReactiveJwtDecoder.withSecretKey(key).build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter authoritiesConverter =
            new JwtGrantedAuthoritiesConverter();

        authoritiesConverter.setAuthorityPrefix("ROLE_");
        authoritiesConverter.setAuthoritiesClaimName("roles");

        ReactiveJwtAuthenticationConverter converter =
            new ReactiveJwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(
            jwt -> Flux.fromIterable(authoritiesConverter.convert(jwt))
        );

        return converter;
    }
}
