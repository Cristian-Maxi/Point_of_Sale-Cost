package com.microservice.pointsalecost.exceptions;

public class InvalidCostException extends RuntimeException {
    public InvalidCostException(String message) {
        super(message);
    }
}
