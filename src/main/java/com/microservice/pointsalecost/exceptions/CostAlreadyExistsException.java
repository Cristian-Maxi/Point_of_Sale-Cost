package com.microservice.pointsalecost.exceptions;

public class CostAlreadyExistsException extends RuntimeException {
    public CostAlreadyExistsException(String message) {
        super(message);
    }
}
