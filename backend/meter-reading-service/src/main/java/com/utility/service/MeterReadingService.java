package com.utility.service;

import com.utility.dto.MeterReadingRequest;
import com.utility.dto.MeterReadingResponse;
import com.utility.dto.PendingMeterReadingResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MeterReadingService {
    Mono<MeterReadingResponse> recordReading(MeterReadingRequest request, String authHeader);
    Flux<PendingMeterReadingResponse> getPendingMeterReadings(String authHeader);
    Flux<MeterReadingResponse> getReadingsByConnection(String connectionId, String authHeader);
    Flux<MeterReadingResponse> getAllReadings();
    Mono<MeterReadingResponse> getReadingById(String readingId);
    Mono<Void> markReadingAsBilled(String readingId);
}
