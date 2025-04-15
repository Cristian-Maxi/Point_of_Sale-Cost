package com.microservice.pointsalecost.dtos.PointOfSaleDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PointOfSaleUpdateDTO(
        @NotNull(message = "ID must not be null")
        Long id,
        @NotBlank(message = "Name must not be empty")
        String name
) {
}