package com.utility.service.impl;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.utility.config.ConnectionClient;
import com.utility.config.ConnectionDTO;
import com.utility.config.ConnectionStatus;
import com.utility.dto.MeterReadingRequest;
import com.utility.dto.MeterReadingResponse;
import com.utility.dto.PendingMeterReadingResponse;
import com.utility.model.MeterReading;
import com.utility.model.ReadingStatus;
import com.utility.repository.MeterReadingRepository;
import com.utility.security.JwtUtil;
import com.utility.service.MeterReadingService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MeterReadingServiceImpl implements MeterReadingService {

	private final MeterReadingRepository repository;
	private final ConnectionClient connectionClient;
	private final JwtUtil jwtUtil;

//	 RECORD METER READINGS
	@Override
	public Mono<MeterReadingResponse> recordReading(MeterReadingRequest request, String authHeader) {

		// Validate reading date
		if (request.getReadingDate().isAfter(LocalDate.now())) {
			return Mono
					.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reading date cannot be in the future"));
		}

		return connectionClient.getConnection(request.getConnectionId(), authHeader)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Connection not found")))
				.flatMap(connection -> {

					// Connection must be ACTIVE
					if (connection.getStatus() != ConnectionStatus.ACTIVE) {
						return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
								"Cannot record reading for inactive connection"));
					}

					// Validate billing cycle window
					YearMonth readingMonth = YearMonth.from(request.getReadingDate());
					YearMonth currentCycle = YearMonth.now();

					if (!readingMonth.equals(currentCycle)) {
						return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
								"Reading must be recorded for current billing cycle"));
					}

					return repository.findTopByConnectionIdOrderByReadingDateDesc(connection.getId())
							.flatMap(lastReading -> {

								// Duplicate reading check (same billing cycle)
								YearMonth lastCycle = YearMonth.from(lastReading.getReadingDate());

								if (lastCycle.equals(readingMonth) && lastReading.getStatus() == ReadingStatus.BILLED) {
									return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT,
											"Reading already billed for this cycle"));
								}

								if (lastCycle.equals(readingMonth)) {
									return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT,
											"Meter reading already recorded for this billing cycle"));
								}

								// Reading date sequence
								if (request.getReadingDate().isBefore(lastReading.getReadingDate())) {
									return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
											"Reading date cannot be before last recorded reading"));
								}

								return Mono.just(lastReading);
							})
							.defaultIfEmpty(
									MeterReading.builder().currentReading(0.0).readingDate(LocalDate.MIN).build())
							.flatMap(lastReading -> {

								double previous = lastReading.getCurrentReading();
								double current = request.getCurrentReading();

								// Reading must increase
								if (current < previous) {
									return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
											"Current reading cannot be less than previous reading"));
								}

								// Units consumed must be positive
								double units = current - previous;
								if (units <= 0) {
									return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
											"Units consumed must be greater than zero"));
								}

								MeterReading reading = MeterReading.builder().connectionId(connection.getId())
										.consumerEmail(connection.getConsumerEmail())
										.utilityType(connection.getUtilityType())
										.meterNumber(connection.getMeterNumber())
										.previousReading(previous)
										.currentReading(current).unitsConsumed(units)
										.readingDate(request.getReadingDate())
										.billingCycle(connection.getBillingCycle()).status(ReadingStatus.RECORDED)
										.build();

								return repository.save(reading);
							});
				}).map(this::toResponse);
	}
	
	@Override
	public Flux<PendingMeterReadingResponse> getPendingMeterReadings(String authHeader) {

	    YearMonth currentMonth = YearMonth.now();
	    LocalDate startOfMonth = currentMonth.atDay(1);
	    LocalDate endOfMonth = currentMonth.atEndOfMonth();

	    return connectionClient.getActiveConnections(authHeader)

	        // keep only connections WITHOUT reading this month
	        .filterWhen(connection ->
	            repository.existsByConnectionIdAndReadingDateBetween(
	                connection.getId(),
	                startOfMonth,
	                endOfMonth
	            ).map(exists -> !exists)
	        )

	        // enrich with last reading info
	        .flatMap(connection ->
	            repository.findTopByConnectionIdOrderByReadingDateDesc(connection.getId())
	                .map(lastReading -> buildPendingResponse(connection, lastReading))
	                .switchIfEmpty(
	                    Mono.just(buildPendingResponse(connection, null))
	                )
	        );
	}
	
	private PendingMeterReadingResponse buildPendingResponse(
	        ConnectionDTO connection,
	        MeterReading lastReading) {

	    return PendingMeterReadingResponse.builder()
	            .connectionId(connection.getId())
	            .consumerEmail(connection.getConsumerEmail())
	            .utilityType(connection.getUtilityType())
	            .meterNumber(connection.getMeterNumber())
	            .billingCycle(connection.getBillingCycle())
	            .lastReadingDate(
	                lastReading != null ? lastReading.getReadingDate() : null
	            )
	            .lastReadingValue(
	                lastReading != null ? lastReading.getCurrentReading() : 0
	            )
	            .build();
	}

	private String extractToken(String authHeader) {
		return authHeader.substring(7); // to remove bearer
	}

	// GET READINGS BY CONNECTION
	@Override
	public Flux<MeterReadingResponse> getReadingsByConnection(String connectionId, String authHeader) {

		String token = extractToken(authHeader);

		String role = jwtUtil.getRole(token);
		String username = jwtUtil.getUsername(token);

		return connectionClient.getConnection(connectionId, authHeader)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Connection not found")))
				.flatMapMany(connection -> {

					// Ownership check for CONSUMER
					if ("CONSUMER".equals(role)) {
						if (!connection.getConsumerEmail().equals(username)) {
							return Flux.error(new ResponseStatusException(HttpStatus.FORBIDDEN,
									"You are not allowed to view readings for this connection"));
						}
					}

					// Fetch readings
					return repository.findByConnectionId(connectionId)
							.sort(Comparator.comparing(MeterReading::getReadingDate)).collectList()
							.flatMapMany(readings -> {

								if (readings.isEmpty()) {
									return Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
											"No meter readings found for this connection"));
								}

								return Flux.fromIterable(readings).map(this::toResponse);
							});
				});
	}

	// GET ALL READINGS
	@Override
	public Flux<MeterReadingResponse> getAllReadings() {

		return repository.findAll()
				.switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No meter readings found")))
				.map(this::toResponse);
	}

	// GET READING BY READING ID
	@Override
	public Mono<MeterReadingResponse> getReadingById(String readingId) {

		return repository.findById(readingId)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Meter reading not found")))
				.map(this::toResponse);
	}

	// MARK READING AS BILLED
	@Override
	public Mono<Void> markReadingAsBilled(String readingId) {
		return repository.findById(readingId)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Meter reading not found")))
				.flatMap(reading -> {

					if (reading.getStatus() == ReadingStatus.BILLED) {
						return Mono.empty();
					}

					reading.setStatus(ReadingStatus.BILLED);
					return repository.save(reading).then();
				});
	}

	private MeterReadingResponse toResponse(MeterReading reading) {
		return MeterReadingResponse.builder()
				.id(reading.getId())
				.connectionId(reading.getConnectionId())
				.consumerEmail(reading.getConsumerEmail())
				.meterNumber(reading.getMeterNumber())
				.utilityType(reading.getUtilityType())
				.previousReading(reading.getPreviousReading())
				.currentReading(reading.getCurrentReading())
				.unitsConsumed(reading.getUnitsConsumed())
				.readingDate(reading.getReadingDate())
				.billingCycle(reading.getBillingCycle())
				.status(reading.getStatus())
				.build();
	}
}