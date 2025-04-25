package com.microservice.pointsalecost.testUtils;

import com.microservice.pointsalecost.enums.RoleEnum;
import com.microservice.pointsalecost.exceptions.AccessDeniedException;
import com.microservice.pointsalecost.utils.RoleValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class RoleValidatorTest {

    private RoleValidator roleValidator;

    @Test
    void testValidateRole_WhenHeaderDoesNotContainAllowedRoles_ThrowsAccessDenied() {
        assertThrows(AccessDeniedException.class, () ->
                RoleValidator.validateRole("ROLE_CLIENT", "No access", RoleEnum.ADMIN));
    }
}
