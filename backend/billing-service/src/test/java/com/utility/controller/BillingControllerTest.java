package com.utility.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.utility.dto.BillGenerateRequest;
import com.utility.dto.BillResponse;
import com.utility.dto.OutstandingBillResponse;
import com.utility.dto.OutstandingUpdateRequest;
import com.utility.dto.TotalOutstandingResponse;
import com.utility.model.BillStatus;
import com.utility.service.BillingService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class BillingControllerTest {

    private WebTestClient webTestClient;
    private BillingService billingService;

    @BeforeEach
    void setUp() {
        billingService = Mockito.mock(BillingService.class);
        BillingController controller = new BillingController(billingService);

        webTestClient = WebTestClient
                .bindToController(controller)
                .build();
    }

    @Test
    void generateBill_shouldReturnBillResponse() {
        BillGenerateRequest request = new BillGenerateRequest();
        request.setMeterReadingId("mr1");

        BillResponse response = BillResponse.builder()
                .id("b1")
                .totalAmount(100)
                .status(BillStatus.GENERATED)
                .build();

        when(billingService.generateBill(any(), eq("Bearer token")))
                .thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/bills/generate")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("b1");
    }

    @Test
    void getAllBills_shouldReturnFlux() {
        when(billingService.getAllBills())
                .thenReturn(Flux.just(BillResponse.builder().id("b1").build()));

        webTestClient.get()
                .uri("/bills")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void outstandingBills_shouldReturnFlux() {
        when(billingService.getOutstandingBills())
                .thenReturn(Flux.just(
                        OutstandingBillResponse.builder()
                                .billId("b1")
                                .remainingAmount(50)
                                .build()
                ));

        webTestClient.get()
                .uri("/bills/outstanding")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void updateOutstanding_shouldReturnOk() {
        OutstandingUpdateRequest request = new OutstandingUpdateRequest();
        request.setOutstandingAmount(25);
        request.setStatus(BillStatus.DUE);

        when(billingService.updateOutstandingAmount(
                eq("b1"), eq(25.0), eq(BillStatus.DUE)))
                .thenReturn(Mono.empty());

        webTestClient.put()
                .uri("/bills/b1/outstanding")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void consumerBills_shouldReturnFlux() {
        when(billingService.getBillsForConsumer(eq("c1"), eq(null)))
                .thenReturn(Flux.just(BillResponse.builder().id("b1").build()));

        webTestClient.get()
                .uri("/bills/consumer/c1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getBillById_shouldReturnBill() {
        when(billingService.getBillById("b1"))
                .thenReturn(Mono.just(BillResponse.builder().id("b1").build()));

        webTestClient.get()
                .uri("/bills/b1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("b1");
    }

    @Test
    void updateStatus_shouldReturnOk() {
        when(billingService.updateBillStatus("b1", BillStatus.PAID))
                .thenReturn(Mono.empty());

        webTestClient.put()
                .uri("/bills/b1/status/PAID")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getTotalOutstanding_shouldReturnTotal() {
        when(billingService.getTotalOutstanding())
                .thenReturn(Mono.just(new TotalOutstandingResponse(500)));

        webTestClient.get()
                .uri("/bills/outstanding/total")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.totalOutstandingAmount").isEqualTo(500.0);
    }
}