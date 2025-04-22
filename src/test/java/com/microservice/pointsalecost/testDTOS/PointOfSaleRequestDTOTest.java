package com.microservice.pointsalecost.testDTOS;

import com.microservice.pointsalecost.dtos.PointOfSaleDTO.PointOfSaleRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

public class PointOfSaleRequestDTOTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidDTO() {
        PointOfSaleRequestDTO dto = new PointOfSaleRequestDTO("Terminal A");
        Set<ConstraintViolation<PointOfSaleRequestDTO>> violations = validator.validate(dto);
        assertEquals(0, violations.size());
    }

    @Test
    public void testInvalidDTO_BlankName() {
        PointOfSaleRequestDTO dto = new PointOfSaleRequestDTO("");
        Set<ConstraintViolation<PointOfSaleRequestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
    }

    @Test
    public void testInvalidDTO_NullName() {
        PointOfSaleRequestDTO dto = new PointOfSaleRequestDTO(null);
        Set<ConstraintViolation<PointOfSaleRequestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
    }
}
