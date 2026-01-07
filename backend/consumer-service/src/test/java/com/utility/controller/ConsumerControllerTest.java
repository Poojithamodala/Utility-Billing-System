package com.utility.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.utility.dto.BillingCycle;
import com.utility.dto.ConnectionRequestByConsumer;
import com.utility.dto.ConsumerGrowthResponse;
import com.utility.dto.ConsumerRegistrationRequestResponse;
import com.utility.dto.ConsumerRequest;
import com.utility.dto.ConsumerResponse;
import com.utility.dto.RejectConsumerRequest;
import com.utility.dto.RequestStatus;
import com.utility.dto.UtilityType;
import com.utility.model.ConnectionRequestEntity;
import com.utility.security.JwtUtil;
import com.utility.service.ConsumerService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class ConsumerControllerTest {

    private WebTestClient webTestClient;
    private ConsumerService consumerService;
    private JwtUtil jwtUtil;

    @BeforeEach
    void setup() {
        consumerService = Mockito.mock(ConsumerService.class);
        jwtUtil = Mockito.mock(JwtUtil.class);

        ConsumerController controller = new ConsumerController(consumerService, jwtUtil);

        webTestClient = WebTestClient
                .bindToController(controller)
                .build();
    }

    @Test
    void requestConsumerRegistration_success() {

        ConsumerRequest request = new ConsumerRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setPhone("9876543210");
        request.setAddress("Hyderabad, India");

        when(consumerService.submitRegistrationRequest(any()))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/consumers/request")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Registration request submitted");
    }

    @Test
    void getAllRequests_success() {
        when(consumerService.getAllRegistrationRequests())
                .thenReturn(Flux.just(
                        ConsumerRegistrationRequestResponse.builder()
                                .id("r1")
                                .status(RequestStatus.PENDING)
                                .createdAt(LocalDateTime.now())
                                .build()
                ));

        webTestClient.get()
                .uri("/consumers/requests")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void approveRequest_success() {
        when(consumerService.approveRequest("r1", "Bearer token"))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/consumers/requests/r1/approve")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void rejectRequest_success() {
        RejectConsumerRequest request = new RejectConsumerRequest();
        request.setReason("Invalid docs");

        when(consumerService.rejectRequest("r1", "Invalid docs"))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/consumers/requests/r1/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void requestConnection_success() {

        ConnectionRequestByConsumer request = new ConnectionRequestByConsumer();
        request.setUtilityType(UtilityType.ELECTRICITY);
        request.setTariffPlanId("tariff-1");
        request.setBillingCycle(BillingCycle.MONTHLY);

        when(consumerService.requestConnection(any(), eq("Bearer token")))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/consumers/request-connection")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Connection request submitted successfully");
    }

    @Test
    void getPendingRequests_success() {
        when(consumerService.getRequestsByStatus(RequestStatus.PENDING))
                .thenReturn(Flux.just(new ConnectionRequestEntity()));

        webTestClient.get()
                .uri("/consumers/connection-requests?status=PENDING")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getRequestById_success() {
        when(consumerService.getRequestById("r1"))
                .thenReturn(Mono.just(new ConnectionRequestEntity()));

        webTestClient.get()
                .uri("/consumers/connection-requests/r1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void updateRequestStatus_success() {
        when(consumerService.updateRequestStatus("r1", RequestStatus.APPROVED))
                .thenReturn(Mono.empty());

        webTestClient.patch()
                .uri("/consumers/connection-requests/r1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(RequestStatus.APPROVED)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void createConsumer_success() {

        ConsumerRequest request = new ConsumerRequest();
        request.setName("Alice");
        request.setEmail("alice@example.com");
        request.setPhone("9876543210");
        request.setAddress("Bangalore");

        ConsumerResponse response = ConsumerResponse.builder()
                .id("c1")
                .build();

        when(consumerService.createConsumer(eq("Bearer token"), any()))
                .thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/consumers")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Consumer created successfully")
                .jsonPath("$.consumerId").isEqualTo("c1");
    }

    @Test
    void getAllConsumers_success() {
        when(consumerService.getAllConsumers())
                .thenReturn(Flux.just(ConsumerResponse.builder().id("c1").build()));

        webTestClient.get()
                .uri("/consumers")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getConsumerById_success() {
        when(consumerService.getConsumerById("c1"))
                .thenReturn(Mono.just(ConsumerResponse.builder().id("c1").build()));

        webTestClient.get()
                .uri("/consumers/c1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void updateConsumer_success() {
        when(consumerService.updateConsumer(eq("c1"), any()))
                .thenReturn(Mono.just(ConsumerResponse.builder().id("c1").build()));

        webTestClient.put()
                .uri("/consumers/c1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ConsumerRequest())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void deleteConsumer_success() {
        when(consumerService.deleteConsumer("c1"))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/consumers/c1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void deleteConsumer_blankId_shouldFail() {
        webTestClient.delete()
                .uri("/consumers/ ")
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void myProfile_success() {
        when(jwtUtil.extractUsername("token"))
                .thenReturn("user1");

        when(consumerService.getMyProfile("user1"))
                .thenReturn(Mono.just(ConsumerResponse.builder().id("c1").build()));

        webTestClient.get()
                .uri("/consumers/profile")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void myProfile_missingHeader_shouldFail() {
        webTestClient.get()
                .uri("/consumers/profile")
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void consumerGrowth_success() {
        when(consumerService.getConsumerGrowth())
                .thenReturn(Flux.just(new ConsumerGrowthResponse("Jan", 10)));

        webTestClient.get()
                .uri("/consumers/reports/growth")
                .exchange()
                .expectStatus().isOk();
    }
}