package com.utility.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "tariffs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TariffPlan {

    @Id
    private String id;

    @NotNull(message = "Utility type is required")
    private UtilityType utilityType;

    @NotBlank(message = "Tariff name is required")
    private String name;

    @PositiveOrZero(message = "Fixed charge cannot be negative")
    private double fixedCharge;

    @Min(value = 0, message = "Tax percentage cannot be less than 0")
    @Max(value = 100, message = "Tax percentage cannot be more than 100")
    private double taxPercentage;

    @NotEmpty(message = "At least one slab is required")
    @Valid
    private List<TariffSlab> slabs;
}