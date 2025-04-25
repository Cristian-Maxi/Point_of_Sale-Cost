package com.microservice.pointsalecost.utils;

import com.microservice.pointsalecost.enums.RoleEnum;
import com.microservice.pointsalecost.exceptions.AccessDeniedException;

public class RoleValidator {
    public static void validateRole(String rolesHeader, String errorMessage, RoleEnum... allowedRoles) {
        for (RoleEnum role : allowedRoles) {
            if (rolesHeader.contains(role.name())) return;
        }
        throw new AccessDeniedException(errorMessage);
    }
}