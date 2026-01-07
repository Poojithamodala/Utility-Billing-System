package com.utility.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.utility.config.AuthClient;
import com.utility.config.ConnectionClient;
import com.utility.dto.BillingCycle;
import com.utility.dto.ConnectionRequestByConsumer;
import com.utility.dto.ConsumerRequest;
import com.utility.dto.RequestStatus;
import com.utility.dto.UtilityType;
import com.utility.model.ConnectionRequestEntity;
import com.utility.model.Consumer;
import com.utility.model.ConsumerRegistrationRequest;
import com.utility.repository.ConnectionRequestRepository;
import com.utility.repository.ConsumerRepository;
import com.utility.repository.ConsumerRequestRepository;
import com.utility.security.JwtUtil;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ConsumerServiceImplTest {

	@Mock
	private ConsumerRepository repository;

	@Mock
	private ConsumerRequestRepository requestRepository;

	@Mock
	private ConnectionRequestRepository connectionRequestRepo;

	@Mock
	private AuthClient authClient;

	@Mock
	private ConnectionClient connectionClient;

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private KafkaTemplate<String, Object> kafkaTemplate;

	@InjectMocks
	private ConsumerServiceImpl service;
	
	@Test
	void submitRegistrationRequest_success() {
	    ConsumerRequest request = new ConsumerRequest();
	    request.setName("John");
	    request.setEmail("john@test.com");
	    request.setPhone("9876543210");
	    request.setAddress("Hyd");

	    when(repository.findByEmail("john@test.com"))
	            .thenReturn(Mono.empty());

	    when(requestRepository.findByEmail("john@test.com"))
	            .thenReturn(Mono.empty());

	    when(requestRepository.save(any()))
	            .thenReturn(Mono.just(new ConsumerRegistrationRequest()));

	    StepVerifier.create(service.submitRegistrationRequest(request))
	            .verifyComplete();
	}
	
	
	
	@Test
	void approveRequest_success() {
	    ConsumerRegistrationRequest req = ConsumerRegistrationRequest.builder()
	            .id("r1")
	            .email("a@test.com")
	            .name("A")
	            .status(RequestStatus.PENDING)
	            .build();

	    Consumer savedConsumer = Consumer.builder()
	            .email("a@test.com")
	            .build();

	    when(requestRepository.findById("r1"))
	            .thenReturn(Mono.just(req));

	    when(repository.save(any()))
	            .thenReturn(Mono.just(savedConsumer));

	    when(authClient.createConsumerUser(any(), any()))
	            .thenReturn(Mono.empty());

	    when(requestRepository.save(any()))
	            .thenReturn(Mono.just(req));

	    StepVerifier.create(service.approveRequest("r1", "Bearer token"))
	            .verifyComplete();
	}
	
	@Test
	void rejectRequest_success() {
	    ConsumerRegistrationRequest req = ConsumerRegistrationRequest.builder()
	            .status(RequestStatus.PENDING)
	            .email("a@test.com")
	            .name("A")
	            .build();

	    when(requestRepository.findById("r1"))
	            .thenReturn(Mono.just(req));

	    when(requestRepository.save(any()))
	            .thenReturn(Mono.just(req));

	    StepVerifier.create(service.rejectRequest("r1", "reason"))
	            .verifyComplete();
	}
	
	@Test
	void requestConnection_success() {
	    Consumer consumer = Consumer.builder()
	            .id("c1")
	            .email("a@test.com")
	            .build();

	    ConnectionRequestByConsumer req = new ConnectionRequestByConsumer();
	    req.setUtilityType(UtilityType.ELECTRICITY);
	    req.setTariffPlanId("t1");
	    req.setBillingCycle(BillingCycle.MONTHLY);

	    when(jwtUtil.extractUsername(any()))
	            .thenReturn("user");

	    when(repository.findByUsername("user"))
	            .thenReturn(Mono.just(consumer));

	    when(connectionClient.hasActiveConnection("c1", UtilityType.ELECTRICITY))
	            .thenReturn(Mono.just(false));

	    when(connectionRequestRepo.existsByConsumerIdAndUtilityTypeAndStatus(
	            "c1", UtilityType.ELECTRICITY, RequestStatus.PENDING))
	            .thenReturn(Mono.just(false));

	    when(connectionRequestRepo.save(any()))
	            .thenReturn(Mono.just(new ConnectionRequestEntity()));

	    StepVerifier.create(service.requestConnection(req, "Bearer token"))
	            .verifyComplete();
	}
	
	@Test
	void createConsumer_success() {
	    ConsumerRequest request = new ConsumerRequest();
	    request.setName("A");
	    request.setEmail("a@test.com");
	    request.setPhone("9876543210");
	    request.setAddress("Hyd");

	    Consumer saved = Consumer.builder()
	            .id("c1")
	            .email("a@test.com")
	            .build();

	    when(repository.existsByEmail("a@test.com"))
	            .thenReturn(Mono.just(false));

	    when(repository.save(any()))
	            .thenReturn(Mono.just(saved));

	    when(authClient.createConsumerUser(any(), any()))
	            .thenReturn(Mono.empty());

	    StepVerifier.create(service.createConsumer("Bearer token", request))
	            .expectNextMatches(res -> res.getId().equals("c1"))
	            .verifyComplete();
	}
	
	@Test
	void getConsumerGrowth_success() {
	    Consumer c = Consumer.builder()
	            .createdAt(LocalDateTime.now())
	            .build();

	    when(repository.findAll())
	            .thenReturn(Flux.just(c));

	    StepVerifier.create(service.getConsumerGrowth())
	            .expectNextCount(1)
	            .verifyComplete();
	}
	
	@Test
	void updateConsumer_shouldUpdateAllFields() {

	    Consumer existing = Consumer.builder()
	            .id("id")
	            .name("Old")
	            .email("old@test.com")
	            .phone("1111111111")
	            .address("OldAddr")
	            .build();

	    ConsumerRequest dto = new ConsumerRequest();
	    dto.setName("New");
	    dto.setEmail("new@test.com");
	    dto.setPhone("9999999999");
	    dto.setAddress("NewAddr");

	    when(repository.findById("id"))
	            .thenReturn(Mono.just(existing));

	    when(repository.existsByEmail("new@test.com"))
	            .thenReturn(Mono.just(false));

	    when(repository.save(any()))
	            .thenReturn(Mono.just(existing));

	    StepVerifier.create(service.updateConsumer("id", dto))
	            .expectNextMatches(res ->
	                    res.getName().equals("New") &&
	                    res.getEmail().equals("new@test.com"))
	            .verifyComplete();
	}
	
	@Test
	void getConsumerById_blank_shouldFail() {
	    StepVerifier.create(service.getConsumerById(" "))
	            .expectError(RuntimeException.class)
	            .verify();
	}

	@Test
	void getConsumerById_invalidFormat_shouldFail() {
	    StepVerifier.create(service.getConsumerById("123"))
	            .expectError(RuntimeException.class)
	            .verify();
	}
	
	@Test
	void deleteConsumer_blankId_shouldFail() {
	    StepVerifier.create(service.deleteConsumer(" "))
	            .expectError(RuntimeException.class)
	            .verify();
	}

	@Test
	void deleteConsumer_notFound_shouldFail() {
	    when(repository.findById("id"))
	            .thenReturn(Mono.empty());

	    StepVerifier.create(service.deleteConsumer("id"))
	            .expectError(ResponseStatusException.class)
	            .verify();
	}
	
	@Test
	void getMyProfile_blankUsername_shouldFail() {
	    StepVerifier.create(service.getMyProfile(""))
	            .expectError(RuntimeException.class)
	            .verify();
	}

	@Test
	void getMyProfile_notFound_shouldFail() {
	    when(repository.findByUsername("user"))
	            .thenReturn(Mono.empty());

	    StepVerifier.create(service.getMyProfile("user"))
	            .expectError(RuntimeException.class)
	            .verify();
	}
	
	@Test
	void getAllConsumers_empty_shouldFail() {
	    when(repository.findAll())
	            .thenReturn(Flux.empty());

	    StepVerifier.create(service.getAllConsumers())
	            .expectError(RuntimeException.class)
	            .verify();
	}
	
	@Test
	void updateRequestStatus_success() {
	    ConnectionRequestEntity entity = new ConnectionRequestEntity();

	    when(connectionRequestRepo.findById("id"))
	            .thenReturn(Mono.just(entity));

	    when(connectionRequestRepo.save(any()))
	            .thenReturn(Mono.just(entity));

	    StepVerifier.create(service.updateRequestStatus("id", RequestStatus.APPROVED))
	            .verifyComplete();
	}
	
	@Test
	void getRequestById_notFound_shouldFail() {
	    when(connectionRequestRepo.findById("id"))
	            .thenReturn(Mono.empty());

	    StepVerifier.create(service.getRequestById("id"))
	            .expectError(ResponseStatusException.class)
	            .verify();
	}

	@Test
	void getRequestById_success() {
	    when(connectionRequestRepo.findById("id"))
	            .thenReturn(Mono.just(new ConnectionRequestEntity()));

	    StepVerifier.create(service.getRequestById("id"))
	            .expectNextCount(1)
	            .verifyComplete();
	}
	
	@Test
	void getAllRegistrationRequests_success() {
	    ConsumerRegistrationRequest req =
	            ConsumerRegistrationRequest.builder()
	                    .id("1")
	                    .status(RequestStatus.PENDING)
	                    .build();

	    when(requestRepository.findByStatus(RequestStatus.PENDING))
	            .thenReturn(Flux.just(req));

	    StepVerifier.create(service.getAllRegistrationRequests())
	            .expectNextCount(1)
	            .verifyComplete();
	}

}
