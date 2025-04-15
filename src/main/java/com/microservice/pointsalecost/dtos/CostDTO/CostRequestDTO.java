package com.microservice.pointsalecost.dtos.CostDTO;

import jakarta.validation.constraints.NotNull;

public record CostRequestDTO(
        @NotNull(message = "ID A must not be null")
        Long idA,
        @NotNull(message = "ID B must not be null")
        Long idB,
        @NotNull(message = "amount must not be null")
        Double amount
) {
}
