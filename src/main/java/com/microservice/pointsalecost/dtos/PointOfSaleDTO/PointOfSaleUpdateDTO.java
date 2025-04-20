package com.microservice.pointsalecost.dtos.PointOfSaleDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PointOfSaleUpdateDTO(
        @Schema(description = "ID of the point of sale to update", example = "5")
        @NotNull(message = "ID must not be null")
        Long id,

        @Schema(description = "New name for the point of sale", example = "Terminal B")
        @NotBlank(message = "Name must not be empty")
        String name
) {
}