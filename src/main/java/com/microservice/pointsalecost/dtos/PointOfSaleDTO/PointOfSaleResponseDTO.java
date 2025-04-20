package com.microservice.pointsalecost.dtos.PointOfSaleDTO;

import io.swagger.v3.oas.annotations.media.Schema;

public record PointOfSaleResponseDTO(
        @Schema(description = "Unique identifier of the point of sale", example = "10")
        Long id,

        @Schema(description = "Name of the point of sale", example = "Central Station")
        String name,

        @Schema(description = "Whether the point of sale is active", example = "true")
        boolean active
) {
}