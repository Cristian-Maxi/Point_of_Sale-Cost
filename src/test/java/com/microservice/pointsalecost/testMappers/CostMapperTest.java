package com.microservice.pointsalecost.testMappers;

import com.microservice.pointsalecost.dtos.CostDTO.CostRequestDTO;
import com.microservice.pointsalecost.dtos.CostDTO.CostResponseDTO;
import com.microservice.pointsalecost.mappers.CostMapper;
import com.microservice.pointsalecost.models.Cost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CostMapperTest {

    @Autowired
    private CostMapper costMapper;

    @Test
    void testToCostResponseDTO() {
        Cost cost = new Cost(1L, 2L, 30.0);

        CostResponseDTO dto = costMapper.toCostResponseDTO(cost);

        assertEquals(1L, dto.idA());
        assertEquals(2L, dto.idB());
        assertEquals(30.0, dto.amount());
    }

    @Test
    void testToEntity() {
        CostRequestDTO dto = new CostRequestDTO(10L, 20L, 50.5);

        Cost entity = costMapper.toEntity(dto);

        assertEquals(10L, entity.getIdA());
        assertEquals(20L, entity.getIdB());
        assertEquals(50.5, entity.getAmount());
    }
}