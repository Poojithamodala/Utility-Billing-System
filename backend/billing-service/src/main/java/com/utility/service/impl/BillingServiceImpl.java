package com.utility.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.utility.config.ConnectionClient;
import com.utility.config.MeterReadingClient;
import com.utility.config.TariffClient;
import com.utility.dto.BillGenerateRequest;
import com.utility.dto.BillGeneratedEvent;
import com.utility.dto.BillResponse;
import com.utility.dto.ConnectionDTO;
import com.utility.dto.ConnectionStatus;
import com.utility.dto.MeterReadingDTO;
import com.utility.dto.OutstandingBillResponse;
import com.utility.dto.TariffPlanDTO;
import com.utility.dto.TotalOutstandingResponse;
import com.utility.kafka.KafkaTopics;
import com.utility.model.Bill;
import com.utility.model.BillStatus;
import com.utility.repository.BillRepository;
import com.utility.service.BillingService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

@Service
@RequiredArgsConstructor
public class BillingServiceImpl implements BillingService {

	private final BillRepository repository;
    private final MeterReadingClient meterReadingClient;
    private final ConnectionClient connectionClient;
    private final TariffClient tariffClient;
    private final TariffCalculator tariffCalculator;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public Mono<BillResponse> generateBill(
            BillGenerateRequest request,
            String authHeader) {

        return repository.existsByMeterReadingId(request.getMeterReadingId())
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Bill already generated for this meter reading"
                    ));
                }
                return Mono.just(request);
            })

            //Fetch meter reading
            .flatMap(req ->
                    meterReadingClient.getReading(
                            req.getMeterReadingId(),
                            authHeader
                    )
            )

            //Fetch connection
            .flatMap(reading ->
                    connectionClient.getConnection(
                            reading.getConnectionId(),
                            authHeader
                    ).map(connection -> Tuples.of(reading, connection))
            )

            //Fetch tariff using tariffPlanId
            .flatMap(tuple -> {

                MeterReadingDTO reading = tuple.getT1();
                ConnectionDTO connection = tuple.getT2();
                
                if (connection.getStatus() != ConnectionStatus.ACTIVE) {
                    return Mono.error(new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Cannot generate bill for inactive connection"
                    ));
                }

                return tariffClient
                        .getTariff(connection.getTariffPlanId(), authHeader)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Tariff plan not found"
                        )))
                        .map(tariff -> Tuples.of(reading, connection, tariff));
            })

            //Calculate bill & save
            .flatMap
            (tuple -> {

                MeterReadingDTO reading = tuple.getT1();
                ConnectionDTO connection = tuple.getT2();
                TariffPlanDTO tariff = tuple.getT3();

                double energyCharge =
                        tariffCalculator.calculateEnergyCharge(
                                reading.getUnitsConsumed(),
                                tariff.getSlabs()
                        );
                double tax = (energyCharge + tariff.getFixedCharge())*tariff.getTaxPercentage() / 100;
                double total = energyCharge + tariff.getFixedCharge() + tax;
                Bill bill = Bill.builder()
                        .consumerId(connection.getConsumerId())
                        .connectionId(connection.getId())
                        .consumerEmail(connection.getConsumerEmail())
                        .utilityType(connection.getUtilityType())
                        .meterReadingId(reading.getId())
                        .unitsConsumed(reading.getUnitsConsumed())
                        .energyCharge(energyCharge)
                        .fixedCharge(tariff.getFixedCharge())
                        .taxAmount(tax)
                        .totalAmount(total)
                        .outstandingAmount(total)
                        .status(BillStatus.GENERATED)
                        .billDate(LocalDate.now())
                        .dueDate(LocalDate.now().plusDays(15))
                        .build();

                return repository.save(bill);
            }).flatMap(bill ->
            meterReadingClient
            .markReadingAsBilled(bill.getMeterReadingId(), authHeader)
            .thenReturn(bill)
            )
            .doOnSuccess(bill -> {

                BillGeneratedEvent event = new BillGeneratedEvent(
                        bill.getId(),
                        bill.getConsumerEmail(),
                        bill.getTotalAmount(),
                        bill.getDueDate()
                );

                kafkaTemplate.send(
                        KafkaTopics.BILL_GENERATED,
                        event
                );
            })
            .map(this::toResponse);
    }
    
    @Override
    public Flux<BillResponse> getAllBills() {
        return repository.findAll()
                .map(this::toResponse);
    }
    
	@Override
	public Mono<Void> updateOutstandingAmount(String billId, double outstandingAmount, BillStatus status) {

		if (outstandingAmount < 0) {
			return Mono.error(
					new ResponseStatusException(HttpStatus.BAD_REQUEST, "Outstanding amount cannot be negative"));
		}

		return repository.findById(billId)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Bill not found")))
				.flatMap(bill -> {
					bill.setOutstandingAmount(outstandingAmount);
					bill.setStatus(status);
					return repository.save(bill).then();
				});
	}
	
	@Override
	public Flux<OutstandingBillResponse> getOutstandingBills() {
		LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
		return repository.findByOutstandingAmountGreaterThan(0).map(bill -> {
			BillStatus computedStatus = bill.getStatus();
			if (today.isAfter(bill.getDueDate())) {
				computedStatus = BillStatus.OVERDUE;
			}
			double paidSoFar = bill.getTotalAmount() - bill.getOutstandingAmount();
	            return OutstandingBillResponse.builder()
	                    .billId(bill.getId())
	                    .consumerEmail(bill.getConsumerEmail())
	                    .utilityType(bill.getUtilityType())
	                    .totalAmount(bill.getTotalAmount())
	                    .paidSoFar(paidSoFar)
	                    .remainingAmount(bill.getOutstandingAmount())
	                    .dueDate(bill.getDueDate())
	                    .status(computedStatus)
	                    .build();
	        });
	}

	@Override
	public Flux<BillResponse> getBillsForConsumer(String consumerId, String authHeader) {
		return repository.findByConsumerId(consumerId).map(this::toResponse);
	}
	
	@Override
	public Mono<Void> updateBillStatus(String billId, BillStatus status) {
		return repository.findById(billId).flatMap(bill -> {
			bill.setStatus(status);
			return repository.save(bill).then();
		});
	}
	
	@Override
	public Mono<BillResponse> getBillById(String billId) {
		if (billId == null || billId.isBlank()) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bill ID is required"));
		}
		return repository.findById(billId)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Bill not found")))
				.map(this::toResponse);
	}
	
	@Override
    public Mono<TotalOutstandingResponse> getTotalOutstanding() {
        return repository
                .findByOutstandingAmountGreaterThan(0)
                .map(Bill::getOutstandingAmount)
                .reduce(0.0, Double::sum)
                .map(TotalOutstandingResponse::new);
    }

    private BillResponse toResponse(Bill bill) {
		BillStatus status = bill.getStatus();
		LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
		if (bill.getOutstandingAmount() > 0 &&
			    today.isAfter(bill.getDueDate())) {
			    status = BillStatus.OVERDUE;
			}
        return BillResponse.builder()
                .id(bill.getId())
                .consumerId(bill.getConsumerId())
                .connectionId(bill.getConnectionId())
                .consumerEmail(bill.getConsumerEmail())
                .utilityType(bill.getUtilityType())
                .unitsConsumed(bill.getUnitsConsumed())
                .totalAmount(bill.getTotalAmount())
                .outstandingAmount(bill.getOutstandingAmount())
                .status(status)
                .billDate(bill.getBillDate())
                .dueDate(bill.getDueDate())
                .build();
    }
}
