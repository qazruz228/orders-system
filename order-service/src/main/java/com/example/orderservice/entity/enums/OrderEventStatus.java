package com.example.orderservice.entity.enums;

public enum OrderEventStatus {
    PENDING,
    CANCELLED,
    COMPLETED;

    public static OrderEventStatus fromString(String orderEventStatusEnum) {
        for (OrderEventStatus orderStatus : OrderEventStatus.values()) {
            if (orderStatus.name().equalsIgnoreCase(orderEventStatusEnum)) {
                return orderStatus;
            }

        }
        throw new IllegalArgumentException("Unknown orderStatus " + orderEventStatusEnum);
    }

}
