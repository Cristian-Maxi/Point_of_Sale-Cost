package com.microservice.pointsalecost.testDTOS;

import com.microservice.pointsalecost.dtos.CostDTO.CostResponseDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class CostResponseDTOTest {

    @Test
    public void testDTOConstruction() {
        Long idA = 1L;
        Long idB = 2L;
        Double amount = 45.5;

        CostResponseDTO dto = new CostResponseDTO(idA, idB, amount);

        assertEquals(idA, dto.idA(), "The source Point of Sale ID should be the same");
        assertEquals(idB, dto.idB(), "The destination Point of Sale ID should be the same");
        assertEquals(amount, dto.amount(), "The cost amount should be the same");
    }

    @Test
    public void testDTOWithNullValues() {
        // Crear una instancia del DTO con valores nulos
        CostResponseDTO dto = new CostResponseDTO(null, null, null);

        assertNull(dto.idA(), "The source Point of Sale ID should be null");
        assertNull(dto.idB(), "The destination Point of Sale ID should be null");
        assertNull(dto.amount(), "The cost amount should be null");
    }

    @Test
    public void testDTOWithZeroAmount() {
        CostResponseDTO dto = new CostResponseDTO(1L, 2L, 0.0);

        assertEquals(1L, dto.idA(), "The source Point of Sale ID should be the same");
        assertEquals(2L, dto.idB(), "The destination Point of Sale ID should be the same");
        assertEquals(0.0, dto.amount(), "The cost amount should be zero");
    }
}
