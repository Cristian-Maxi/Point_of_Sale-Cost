package com.microservice.pointsalecost.dtos.CostDTO;

import com.microservice.pointsalecost.dtos.PointOfSaleDTO.PointOfSaleResponseDTO;

import java.util.List;

public record CostMinimumDTO(
        List<PointOfSaleResponseDTO> pointOfSaleResponse,
        Double totalCost
) {
}
