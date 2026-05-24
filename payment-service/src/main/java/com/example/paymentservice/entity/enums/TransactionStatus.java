package com.example.paymentservice.entity.enums;

public enum TransactionStatus {
    PENDING,
    COMPLETED,
    CANCELLED,
    FAILED;

    public static TransactionStatus fromString(String status) {
        for (TransactionStatus transactionStatus : TransactionStatus.values()) {
            if (transactionStatus.name().equalsIgnoreCase(status)) {
                return transactionStatus;
            }
        }
        throw new IllegalArgumentException("Unknown transaction status " + status);
    }
}
