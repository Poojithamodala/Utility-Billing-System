package com.utility.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final String ERROR_KEY = "error";

    @ExceptionHandler(ConsumerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<String> handleNotFound(ConsumerNotFoundException ex) {
        return Mono.just(ex.getMessage());
    }
    
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<Map<String, String>> handleValidationException(WebExchangeBindException ex) {

        // return simple map of first error only
        String errorMsg = ex.getFieldErrors().stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        return Mono.just(Map.of(ERROR_KEY, errorMsg));
    }
    
    @ExceptionHandler(RuntimeException.class)
    public Mono<Map<String, String>> handleRuntime(RuntimeException ex) {
        return Mono.just(Map.of(ERROR_KEY, ex.getMessage()));
    }
    
    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleResponseStatusException(
            ResponseStatusException ex) {

        return Mono.just(
                ResponseEntity
                        .status(ex.getStatusCode())
                        .body(Map.of(ERROR_KEY, ex.getReason()))
        );
    }
}
