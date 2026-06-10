package com.example.paymentservice.events;

import com.example.paymentservice.events.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentProcessedEvent {

    private Long orderId;

    private String uniqueOrderNumber;

    private PaymentStatus paymentStatus;
}
