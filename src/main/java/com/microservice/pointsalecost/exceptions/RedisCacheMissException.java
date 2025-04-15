package com.microservice.pointsalecost.exceptions;

public class RedisCacheMissException extends RuntimeException {
    public RedisCacheMissException(String message) {
        super(message);
    }
}
