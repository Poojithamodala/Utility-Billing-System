package com.utility.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utility.model.TariffPlan;
import com.utility.model.UtilityType;
import com.utility.service.TariffService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/tariffs")
@RequiredArgsConstructor
public class TariffController {

	private final TariffService service;

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public Mono<ResponseEntity<String>> create(@RequestBody TariffPlan tariff) {
		return service.createTariff(tariff).map(saved -> ResponseEntity.ok("Tariff created successfully"))
				.onErrorResume(ex -> Mono.just(ResponseEntity.badRequest().body(ex.getMessage())));
	}

	@GetMapping("/utility/{utilityType}")
	@PreAuthorize("hasAnyRole('ADMIN', 'BILLING_OFFICER', 'CONSUMER')")
	public Flux<TariffPlan> byUtility(@PathVariable UtilityType utilityType) {
		return service.getTariffsByUtility(utilityType);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'BILLING_OFFICER', 'CONSUMER')")
	public Mono<TariffPlan> getTariffById(@PathVariable String id) {
		return service.getTariffById(id);
	}
}
