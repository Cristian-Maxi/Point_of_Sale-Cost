package com.microservice.pointsalecost.dtos.CostDTO;

public record CostResponseDTO(
        Long idA,
        Long idB,
        Double amount
) {
}
