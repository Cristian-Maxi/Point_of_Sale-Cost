package com.microservice.pointsalecost.dtos.CostDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CostRequestDTO(
        @Schema(description = "ID of the source point", example = "1")
        @NotNull(message = "ID A must not be null")
        Long idA,

        @Schema(description = "ID of the destination point", example = "2")
        @NotNull(message = "ID B must not be null")
        Long idB,

        @Schema(description = "Cost amount between the points", example = "15.50")
        @NotNull(message = "amount must not be null")
        Double amount
) {
}
