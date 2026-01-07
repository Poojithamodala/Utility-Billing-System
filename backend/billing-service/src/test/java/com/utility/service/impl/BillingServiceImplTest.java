package com.utility.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.utility.config.ConnectionClient;
import com.utility.config.MeterReadingClient;
import com.utility.config.TariffClient;
import com.utility.dto.BillGenerateRequest;
import com.utility.dto.ConnectionDTO;
import com.utility.dto.ConnectionStatus;
import com.utility.dto.MeterReadingDTO;
import com.utility.dto.TariffPlanDTO;
import com.utility.model.Bill;
import com.utility.model.BillStatus;
import com.utility.model.UtilityType;
import com.utility.repository.BillRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class BillingServiceImplTest {

    private BillingServiceImpl service;

    private BillRepository repository;
    private MeterReadingClient meterReadingClient;
    private ConnectionClient connectionClient;
    private TariffClient tariffClient;
    private TariffCalculator tariffCalculator;
    private KafkaTemplate<String, Object> kafkaTemplate;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(BillRepository.class);
        meterReadingClient = Mockito.mock(MeterReadingClient.class);
        connectionClient = Mockito.mock(ConnectionClient.class);
        tariffClient = Mockito.mock(TariffClient.class);
        tariffCalculator = Mockito.mock(TariffCalculator.class);
        kafkaTemplate = Mockito.mock(KafkaTemplate.class);

        service = new BillingServiceImpl(
                repository,
                meterReadingClient,
                connectionClient,
                tariffClient,
                tariffCalculator,
                kafkaTemplate
        );
    }

    @Test
    void generateBill_billAlreadyExists() {
        when(repository.existsByMeterReadingId("mr1"))
                .thenReturn(Mono.just(true));

        StepVerifier.create(service.generateBill(
                new BillGenerateRequest("mr1"), "token"))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    void generateBill_inactiveConnection() {
        when(repository.existsByMeterReadingId("mr1"))
                .thenReturn(Mono.just(false));

        MeterReadingDTO reading = new MeterReadingDTO();
        reading.setId("mr1");
        reading.setConnectionId("c1");

        ConnectionDTO connection = new ConnectionDTO();
        connection.setStatus(ConnectionStatus.INACTIVE);

        when(meterReadingClient.getReading("mr1", "token"))
                .thenReturn(Mono.just(reading));

        when(connectionClient.getConnection("c1", "token"))
                .thenReturn(Mono.just(connection));

        StepVerifier.create(service.generateBill(
                new BillGenerateRequest("mr1"), "token"))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    void generateBill_tariffNotFound() {
        when(repository.existsByMeterReadingId("mr1"))
                .thenReturn(Mono.just(false));

        MeterReadingDTO reading = new MeterReadingDTO();
        reading.setId("mr1");
        reading.setConnectionId("c1");
        reading.setUnitsConsumed(100);

        ConnectionDTO connection = new ConnectionDTO();
        connection.setId("c1");
        connection.setTariffPlanId("t1");
        connection.setStatus(ConnectionStatus.ACTIVE);

        when(meterReadingClient.getReading("mr1", "token"))
                .thenReturn(Mono.just(reading));

        when(connectionClient.getConnection("c1", "token"))
                .thenReturn(Mono.just(connection));

        when(tariffClient.getTariff("t1", "token"))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.generateBill(
                new BillGenerateRequest("mr1"), "token"))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    void generateBill_success() {
        when(repository.existsByMeterReadingId("mr1"))
                .thenReturn(Mono.just(false));

        MeterReadingDTO reading = new MeterReadingDTO();
        reading.setId("mr1");
        reading.setConnectionId("c1");
        reading.setUnitsConsumed(100);

        ConnectionDTO connection = new ConnectionDTO();
        connection.setId("c1");
        connection.setConsumerId("u1");
        connection.setConsumerEmail("a@b.com");
        connection.setUtilityType(UtilityType.ELECTRICITY);
        connection.setTariffPlanId("t1");
        connection.setStatus(ConnectionStatus.ACTIVE);

        TariffPlanDTO tariff = new TariffPlanDTO();
        tariff.setFixedCharge(50);
        tariff.setTaxPercentage(10);
        tariff.setSlabs(List.of());

        Bill savedBill = Bill.builder()
                .id("b1")
                .meterReadingId("mr1")
                .outstandingAmount(150)
                .dueDate(LocalDate.now().plusDays(10))
                .build();
        
        when(meterReadingClient.markReadingAsBilled(eq("mr1"), eq("token")))
        .thenReturn(Mono.empty());

        when(meterReadingClient.getReading("mr1", "token"))
                .thenReturn(Mono.just(reading));

        when(connectionClient.getConnection("c1", "token"))
                .thenReturn(Mono.just(connection));

        when(tariffClient.getTariff("t1", "token"))
                .thenReturn(Mono.just(tariff));

        when(tariffCalculator.calculateEnergyCharge(eq(100.0), any()))
                .thenReturn(100.0);

        when(repository.save(any()))
                .thenReturn(Mono.just(savedBill));

        when(meterReadingClient.markReadingAsBilled("mr1", "token"))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.generateBill(
                new BillGenerateRequest("mr1"), "token"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void updateOutstanding_negativeAmount() {
        StepVerifier.create(service.updateOutstandingAmount("b1", -1, BillStatus.PAID))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    void updateOutstanding_billNotFound() {
        when(repository.findById("b1"))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.updateOutstandingAmount("b1", 10, BillStatus.PAID))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    void updateOutstanding_success() {
        when(repository.findById("b1"))
                .thenReturn(Mono.just(new Bill()));

        when(repository.save(any()))
                .thenReturn(Mono.just(new Bill()));

        StepVerifier.create(service.updateOutstandingAmount("b1", 10, BillStatus.PAID))
                .verifyComplete();
    }

    @Test
    void getOutstandingBills_overdue() {
        Bill bill = Bill.builder()
                .id("b1")
                .totalAmount(100)
                .outstandingAmount(50)
                .dueDate(LocalDate.now(ZoneId.of("Asia/Kolkata")).minusDays(1))
                .build();

        when(repository.findByOutstandingAmountGreaterThan(0))
                .thenReturn(Flux.just(bill));

        StepVerifier.create(service.getOutstandingBills())
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getBillById_blankId() {
        StepVerifier.create(service.getBillById(" "))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    void getBillById_notFound() {
        when(repository.findById("b1"))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.getBillById("b1"))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    void getBillById_success() {
        when(repository.findById("b1"))
                .thenReturn(Mono.just(Bill.builder().id("b1").build()));

        StepVerifier.create(service.getBillById("b1"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getAllBills() {
        when(repository.findAll())
                .thenReturn(Flux.just(new Bill()));

        StepVerifier.create(service.getAllBills())
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getBillsForConsumer() {
        when(repository.findByConsumerId("c1"))
                .thenReturn(Flux.just(new Bill()));

        StepVerifier.create(service.getBillsForConsumer("c1", null))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void updateBillStatus() {
        when(repository.findById("b1"))
                .thenReturn(Mono.just(new Bill()));

        when(repository.save(any()))
                .thenReturn(Mono.just(new Bill()));

        StepVerifier.create(service.updateBillStatus("b1", BillStatus.PAID))
                .verifyComplete();
    }

    @Test
    void getTotalOutstanding() {
        when(repository.findByOutstandingAmountGreaterThan(0))
                .thenReturn(Flux.just(
                        Bill.builder().outstandingAmount(50).build(),
                        Bill.builder().outstandingAmount(25).build()
                ));

        StepVerifier.create(service.getTotalOutstanding())
                .expectNextMatches(r -> r.getTotalOutstandingAmount() == 75)
                .verifyComplete();
    }
}