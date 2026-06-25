package com.example.orderservice.events.enums;

public enum PaymentStatus {
    SUCCEEDED,
    CANCELLED;

    public static PaymentStatus fromString(String value) {
        for (PaymentStatus status : values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown payment status " + value);
    }
}
