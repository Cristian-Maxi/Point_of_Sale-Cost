package com.microservice.pointsalecost.exceptions;

public class PointOfSaleNotFoundException extends RuntimeException {
    public PointOfSaleNotFoundException(String message) {
        super(message);
    }
}
