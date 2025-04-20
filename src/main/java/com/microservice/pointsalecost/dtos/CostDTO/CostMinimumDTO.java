package com.microservice.pointsalecost.dtos.CostDTO;

import com.microservice.pointsalecost.dtos.PointOfSaleDTO.PointOfSaleResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CostMinimumDTO(
        @Schema(description = "List of points forming the minimum cost path")
        List<PointOfSaleResponseDTO> pointOfSaleResponse,

        @Schema(description = "Total cost of the path", example = "37.80")
        Double totalCost
) {
}