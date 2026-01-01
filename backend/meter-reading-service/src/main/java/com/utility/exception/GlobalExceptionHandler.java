package com.utility.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidMeterReadingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<String> handleInvalidReading(InvalidMeterReadingException ex) {
        return Mono.just(ex.getMessage());
    }
}
