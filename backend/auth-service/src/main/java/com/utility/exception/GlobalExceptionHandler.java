package com.utility.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResponseStatusException.class)
	public Mono<ResponseEntity<Map<String, Object>>> handleResponseStatusException(ResponseStatusException ex) {
		Map<String, Object> body = new HashMap<>();
		body.put("error", ex.getReason());
		return Mono.just(ResponseEntity.status(ex.getStatusCode()).body(body));
	}

	@ExceptionHandler(DuplicateKeyException.class)
	public Mono<ResponseEntity<Map<String, Object>>> handleDuplicateKey(DuplicateKeyException ex) {
		Map<String, Object> body = new HashMap<>();
		body.put("error", "Username or Email already exists");
		return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(body));
	}

	@ExceptionHandler(Exception.class)
	public Mono<ResponseEntity<Map<String, Object>>> handleGenericException(Exception ex) {
		Map<String, Object> body = new HashMap<>();
		body.put("error", ex.getMessage());
		return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body));
	}

	@ExceptionHandler(WebExchangeBindException.class)
	public Mono<ResponseEntity<Map<String, String>>> handleValidationException(WebExchangeBindException ex) {
		Map<String, String> errorMap = ex.getFieldErrors().stream().collect(
				Collectors.toMap(fieldError -> fieldError.getField(), fieldError -> fieldError.getDefaultMessage()));
		return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap));
	}
}