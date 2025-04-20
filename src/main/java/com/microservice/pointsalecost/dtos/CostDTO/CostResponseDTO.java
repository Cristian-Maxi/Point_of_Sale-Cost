package com.microservice.pointsalecost.dtos.CostDTO;

import io.swagger.v3.oas.annotations.media.Schema;

public record CostResponseDTO(
        @Schema(description = "ID of the source Point of Sale", example = "1")
        Long idA,

        @Schema(description = "ID of the destination Point of Sale", example = "2")
        Long idB,

        @Schema(description = "Cost amount between points", example = "45.5")
        Double amount
) {
}
