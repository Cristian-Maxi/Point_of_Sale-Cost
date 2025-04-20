package com.microservice.pointsalecost.dtos.PointOfSaleDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record PointOfSaleRequestDTO(
        @Schema(description = "Name of the new point of sale", example = "Terminal A")
        @NotBlank(message = "Name must not be empty")
        String name
) {
}