package com.utility.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TariffSlab {
    private int fromUnit;
    private int toUnit;
    private double rate;
}
