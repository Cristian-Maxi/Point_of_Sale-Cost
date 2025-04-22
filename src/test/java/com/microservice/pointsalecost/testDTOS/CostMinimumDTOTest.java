package com.microservice.pointsalecost.testDTOS;

import com.microservice.pointsalecost.dtos.CostDTO.CostMinimumDTO;
import com.microservice.pointsalecost.dtos.PointOfSaleDTO.PointOfSaleResponseDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class CostMinimumDTOTest {

    @Test
    public void testCostMinimumDTO() {
        PointOfSaleResponseDTO point1 = new PointOfSaleResponseDTO(1L, "Terminal A", true);
        PointOfSaleResponseDTO point2 = new PointOfSaleResponseDTO(2L, "Terminal B", true);

        CostMinimumDTO dto = new CostMinimumDTO(List.of(point1, point2), 37.80);

        assertEquals(2, dto.pointOfSaleResponse().size(), "The list of points of sale should contain two elements");

        assertEquals(37.80, dto.totalCost(), "The total cost should be the same");
    }
}
