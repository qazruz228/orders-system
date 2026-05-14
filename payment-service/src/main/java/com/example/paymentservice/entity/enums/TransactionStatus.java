package com.example.paymentservice.entity.enums;

import com.example.paymentservice.repository.TransactionRepository;

public enum TransactionStatus {
    SUCCESS,
    FAILED;

    public static TransactionStatus fromString(String status) {
        for (TransactionStatus transactionStatus :TransactionStatus.values()) {
            if (transactionStatus.name().equalsIgnoreCase(status)) {
                return transactionStatus;
            }

        }
        throw new IllegalArgumentException("Unknown orderStatus " + transactionStatus);
    }

}