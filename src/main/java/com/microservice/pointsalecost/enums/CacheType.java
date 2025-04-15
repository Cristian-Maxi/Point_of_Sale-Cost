package com.microservice.pointsalecost.enums;

public enum CacheType {
    POINT_OF_SALE("point_of_sale"),
    COST("cost");

    private final String values;

    CacheType(String values) {
        this.values = values;
    }

    public String getValues() {
        return values;
    }
}