package com.microservice.pointsalecost.dtos.PointOfSaleDTO;

import jakarta.validation.constraints.NotBlank;

public record PointOfSaleRequestDTO(
        @NotBlank(message = "Name must not be empty")
        String name
) {
}