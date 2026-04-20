package com.example.orderservice.entity.enums;

public enum OrderStatus {
    NEW,
    PROCESSING,
    PAID,
    CANCELLED,
    COMPLETED;

    public static OrderStatus fromString(String orderStatusEnum) {
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus.name().equalsIgnoreCase(orderStatusEnum)) {
                return orderStatus;
            }

        }
        throw new IllegalArgumentException("Unknown orderStatus " + orderStatusEnum);
    }

}
