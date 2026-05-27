package com.example.orderservice.events;

import com.example.orderservice.events.enums.PaymentStatus;
import lombok.Data;

@Data
public class PaymentProcessedEvent {
    private Long orderId;
    private String uniqueOrderNumber;
    private PaymentStatus paymentStatus;
}
