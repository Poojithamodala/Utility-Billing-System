package com.utility.service.impl;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.utility.config.ConnectionClient;
import com.utility.config.MeterReadingClient;
import com.utility.config.TariffClient;
import com.utility.dto.BillGenerateRequest;
import com.utility.dto.BillResponse;
import com.utility.dto.ConnectionDTO;
import com.utility.dto.MeterReadingDTO;
import com.utility.dto.TariffPlanDTO;
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

                return tariffClient
                        .getTariff(connection.getTariffPlanId(), authHeader)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Tariff plan not found"
                        )))
                        .map(tariff -> Tuples.of(reading, connection, tariff));
            })

            //Calculate bill & save
            .flatMap(tuple -> {

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
                        .utilityType(connection.getUtilityType())
                        .meterReadingId(reading.getId())
                        .unitsConsumed(reading.getUnitsConsumed())
                        .energyCharge(energyCharge)
                        .fixedCharge(tariff.getFixedCharge())
                        .taxAmount(tax)
                        .totalAmount(total)
                        .status(BillStatus.GENERATED)
                        .billDate(LocalDate.now())
                        .dueDate(LocalDate.now().plusDays(15))
                        .build();

                return repository.save(bill);
            })
            .map(this::toResponse);
    }

	@Override
	public Flux<BillResponse> getBillsForConsumer(String consumerId, String authHeader) {
		return repository.findByConsumerId(consumerId).map(this::toResponse);
	}

    private BillResponse toResponse(Bill bill) {
        return BillResponse.builder()
                .id(bill.getId())
                .consumerId(bill.getConsumerId())
                .connectionId(bill.getConnectionId())
                .utilityType(bill.getUtilityType())
                .unitsConsumed(bill.getUnitsConsumed())
                .totalAmount(bill.getTotalAmount())
                .status(bill.getStatus())
                .billDate(bill.getBillDate())
                .dueDate(bill.getDueDate())
                .build();
    }
}
