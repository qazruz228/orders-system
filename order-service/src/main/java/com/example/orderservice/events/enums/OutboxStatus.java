package com.example.orderservice.events.enums;

public enum OutboxStatus {
    NEW,
    PROCESSING,
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
