package com.microservice.pointsalecost.testDTOS;

import com.microservice.pointsalecost.dtos.PointOfSaleDTO.PointOfSaleUpdateDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PointOfSaleUpdateDTOTest {

    @Test
    public void testPointOfSaleUpdateDTO() {
        Long id = 5L;
        String name = "Terminal B";

        PointOfSaleUpdateDTO dto = new PointOfSaleUpdateDTO(id, name);

        assertEquals(id, dto.id(), "The Point of Sale ID should be the same");
        assertEquals(name, dto.name(), "The Point of Sale name should be the same");
    }

    @Test
    public void testPointOfSaleUpdateDTOWithNullId() {
        PointOfSaleUpdateDTO dto = new PointOfSaleUpdateDTO(null, "Terminal B");
        assertNull(dto.id(), "The Point of Sale ID should be null");
    }

    @Test
    public void testPointOfSaleUpdateDTOWithBlankName() {
        PointOfSaleUpdateDTO dto = new PointOfSaleUpdateDTO(5L, "");
        assertEquals("", dto.name(), "The name should be an empty string");
    }

    @Test
    public void testPointOfSaleUpdateDTOWithValidValues() {
        PointOfSaleUpdateDTO dto = new PointOfSaleUpdateDTO(5L, "Terminal B");

        assertEquals(5L, dto.id(), "The Point of Sale ID should be 5");
        assertEquals("Terminal B", dto.name(), "The name should be 'Terminal B'");
    }
}
