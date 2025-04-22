package com.microservice.pointsalecost.testDTOS;

import com.microservice.pointsalecost.dtos.PointOfSaleDTO.PointOfSaleResponseDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PointOfSaleResponseDTOTest {

    @Test
    public void testDTOConstruction() {
        Long id = 10L;
        String name = "Central Station";
        boolean active = true;

        PointOfSaleResponseDTO dto = new PointOfSaleResponseDTO(id, name, active);

        assertEquals(id, dto.id(), "The ID should be the same");
        assertEquals(name, dto.name(), "The name should be the same");
        assertTrue(dto.active(), "The active status should be true");
    }

    @Test
    public void testDTOWithInactiveStatus() {
        PointOfSaleResponseDTO dto = new PointOfSaleResponseDTO(5L, "Sucursal Sur", false);

        assertEquals(5L, dto.id(), "The ID should be the same");
        assertEquals("Sucursal Sur", dto.name(), "The name should be the same");
        assertFalse(dto.active(), "The active status should be false");
    }

    @Test
    public void testDTOWithNullValues() {
        PointOfSaleResponseDTO dto = new PointOfSaleResponseDTO(null, null, false);

        assertNull(dto.id(), "The ID should be null");
        assertNull(dto.name(), "The name should be null");
        assertFalse(dto.active(), "The active status should be false");
    }
}