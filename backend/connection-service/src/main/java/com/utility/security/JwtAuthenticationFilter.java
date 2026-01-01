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

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

	private final JwtUtil jwtUtil;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

		String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

		if (header == null || !header.startsWith("Bearer ")) {
			return chain.filter(exchange);
		}

		String token = header.substring(7);

		if (!jwtUtil.isTokenValid(token)) {
			return chain.filter(exchange);
		}

		String username = jwtUtil.getUsername(token);
		String role = jwtUtil.getRole(token);

		Authentication auth = new UsernamePasswordAuthenticationToken(username, null,
				List.of(new SimpleGrantedAuthority("ROLE_" + role)));
		return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
	}
}