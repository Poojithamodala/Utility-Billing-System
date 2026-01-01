package com.utility.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TariffSlab {

	@Min(value = 0, message = "From unit must be >= 0")
    private int fromUnit;

    @Min(value = 1, message = "To unit must be >= 1")
    private int toUnit;

    @Positive(message = "Rate must be greater than 0")
    private double rate;
}