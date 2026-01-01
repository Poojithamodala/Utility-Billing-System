package com.utility.security;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

	private final JwtUtil jwtUtil;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

		String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return chain.filter(exchange);
		}

		String token = authHeader.substring(7);

		Claims claims;
		try {
			claims = jwtUtil.validateToken(token);
		} catch (Exception e) {
			return chain.filter(exchange); // invalid token
		}

		String username = claims.getSubject();
		String role = claims.get("role", String.class);

		Authentication authentication = new UsernamePasswordAuthenticationToken(username, null,
				List.of(new SimpleGrantedAuthority("ROLE_" + role)));

		return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
	}
}