package com.microservice.pointsalecost.testMappers;

import com.microservice.pointsalecost.dtos.PointOfSaleDTO.PointOfSaleRequestDTO;
import com.microservice.pointsalecost.dtos.PointOfSaleDTO.PointOfSaleResponseDTO;
import com.microservice.pointsalecost.mappers.PointOfSaleMapper;
import com.microservice.pointsalecost.models.PointOfSale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PointOfSaleMapperTest {

    @Autowired
    private PointOfSaleMapper pointOfSaleMapper;

    @Test
    void testToPointOfSaleResponseDTO() {
        PointOfSale entity = new PointOfSale(1L, "Terminal X", true);

        PointOfSaleResponseDTO dto = pointOfSaleMapper.toPointOfSaleResponseDTO(entity);

        assertEquals(1L, dto.id());
        assertEquals("Terminal X", dto.name());
        assertTrue(dto.active());
    }

    @Test
    void testToEntity() {
        PointOfSaleRequestDTO dto = new PointOfSaleRequestDTO("Nuevo Terminal");

        PointOfSale entity = pointOfSaleMapper.toEntity(dto);

        assertEquals("Nuevo Terminal", entity.getName());
        assertFalse(entity.isActive());
    }
}
