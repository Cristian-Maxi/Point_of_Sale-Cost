package com.microservice.pointsalecost.dtos.PointOfSaleDTO;

public record PointOfSaleResponseDTO(
        Long id,
        String name,
        boolean active
) {
}