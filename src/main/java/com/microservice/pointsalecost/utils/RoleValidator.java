package com.microservice.pointsalecost.utils;

import com.microservice.pointsalecost.exceptions.AccessDeniedException;

public class RoleValidator {
    public static void validateRole(String rolesHeader, String errorMessage, String... allowedRoles) {
        for (String allowed : allowedRoles) {
            if (rolesHeader.contains(allowed)) return;
        }
        throw new AccessDeniedException(errorMessage);
    }
}