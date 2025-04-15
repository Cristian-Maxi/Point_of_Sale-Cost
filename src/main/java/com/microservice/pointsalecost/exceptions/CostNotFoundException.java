package com.microservice.pointsalecost.exceptions;

public class CostNotFoundException extends RuntimeException {
    public CostNotFoundException(String message) {
        super(message);
    }
}
