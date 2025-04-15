package com.microservice.pointsalecost.exceptions;

public class InactivePointOfSaleException extends RuntimeException {
    public InactivePointOfSaleException(String message) {
        super(message);
    }
}
