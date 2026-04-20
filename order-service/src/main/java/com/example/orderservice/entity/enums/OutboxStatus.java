package com.example.orderservice.entity.enums;

public enum OutboxStatus {
    NEW,
    SENT,
    FAILED;


    public static OutboxStatus fromString(String outboxStatusEnum){

        for (OutboxStatus outboxStatus : OutboxStatus.values()){
            if (outboxStatus.name().equalsIgnoreCase(outboxStatusEnum)){
                return outboxStatus;
            }
        }
        throw new IllegalArgumentException("unknown outboxStatus " + outboxStatusEnum);
    }
}
