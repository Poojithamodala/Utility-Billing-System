package com.utility.service;

import com.utility.model.TariffPlan;
import com.utility.model.UtilityType;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TariffService {
	Mono<TariffPlan> createTariff(TariffPlan tariff);
	Flux<TariffPlan> getTariffsByUtility(UtilityType utilityType);
	Mono<TariffPlan> getTariffById(String id);
}