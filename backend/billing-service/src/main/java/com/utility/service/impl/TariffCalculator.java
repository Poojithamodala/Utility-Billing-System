package com.utility.service.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.utility.dto.TariffSlab;

@Component
public class TariffCalculator {

    public double calculateEnergyCharge(double units, List<TariffSlab> slabs) {
        double amount = 0;
        double remaining = units;
        for (TariffSlab slab : slabs) {
            if (remaining <= 0) break;
            double slabUnits = Math.min(
                    remaining,
                    slab.getToUnit() - slab.getFromUnit() + 1
            );
            amount += slabUnits * slab.getRate();
            remaining -= slabUnits;
        }
        return amount;
    }
}