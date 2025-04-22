package com.microservice.pointsalecost.testDTOS;

import com.microservice.pointsalecost.dtos.CostDTO.CostRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CostRequestDTOTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidDTO() {
        CostRequestDTO dto = new CostRequestDTO(1L, 2L, 15.50);
        Set<ConstraintViolation<CostRequestDTO>> violations = validator.validate(dto);
        assertEquals(0, violations.size());
    }

    @Test
    public void testInvalidDTO_NullIdA() {
        CostRequestDTO dto = new CostRequestDTO(null, 2L, 15.50);
        Set<ConstraintViolation<CostRequestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
    }

    @Test
    public void testInvalidDTO_NullIdB() {
        CostRequestDTO dto = new CostRequestDTO(1L, null, 15.50);
        Set<ConstraintViolation<CostRequestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
    }

    @Test
    public void testInvalidDTO_NullAmount() {
        CostRequestDTO dto = new CostRequestDTO(1L, 2L, null);
        Set<ConstraintViolation<CostRequestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
    }
}
