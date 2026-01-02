package com.utility.service.impl;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.utility.model.TariffPlan;
import com.utility.model.TariffSlab;
import com.utility.model.UtilityType;
import com.utility.repository.TariffRepository;
import com.utility.service.TariffService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TariffServiceImpl implements TariffService {

	private final TariffRepository repository;

	@Override
	public Mono<TariffPlan> createTariff(TariffPlan tariff) {
		List<TariffSlab> slabs = tariff.getSlabs();
		slabs.sort(Comparator.comparingInt(TariffSlab::getFromUnit));
		if (slabs.get(0).getFromUnit() != 0) {
			return Mono.error(new IllegalArgumentException("First slab must start from unit 0"));
		}
		Set<String> ranges = new HashSet<>();
		for (int i = 0; i < slabs.size(); i++) {
			TariffSlab slab = slabs.get(i);
			if (slab.getFromUnit() > slab.getToUnit()) {
				return Mono.error(new IllegalArgumentException("Invalid slab range"));
			}
			String key = slab.getFromUnit() + "-" + slab.getToUnit();
			if (!ranges.add(key)) {
				return Mono.error(new IllegalArgumentException("Duplicate slab ranges not allowed"));
			}

			if (i > 0) {
				TariffSlab prev = slabs.get(i - 1);
				if (slab.getFromUnit() <= prev.getToUnit()) {
					return Mono.error(new IllegalArgumentException("Slab overlap detected"));
				}
				if (slab.getFromUnit() != prev.getToUnit() + 1) {
					return Mono.error(new IllegalArgumentException("Slabs must be continuous"));
				}
			}
		}
		return repository.existsByUtilityTypeAndName(tariff.getUtilityType(), tariff.getName()).flatMap(exists -> {
			if (exists) {
				return Mono.error(new IllegalArgumentException("Tariff plan already exists for this utility"));
			}
			return repository.save(tariff);
		});
	}

	@Override
	public Flux<TariffPlan> getTariffsByUtility(UtilityType utilityType) {
		return repository.findByUtilityType(utilityType);
	}

	@Override
	public Mono<TariffPlan> getTariffById(String id) {

		return repository.findById(id)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Tariff plan not found")));
	}
}