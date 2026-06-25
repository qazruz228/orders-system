package com.example.paymentservice.util.validator;

import com.example.paymentservice.events.OrderEvent;
import com.example.paymentservice.events.enums.OrderEventStatus;
import com.example.paymentservice.util.converter.JsonConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventValidator {

    private final JsonConverter jsonConverter;

    public OrderEvent validate(String value, OrderEventStatus expectedStatus) {
        OrderEvent orderEvent = jsonConverter.fromJson(value, OrderEvent.class);
        if (orderEvent.getOrderId() == null) {
            throw new IllegalArgumentException("orderId must not be null");
        }
        if (orderEvent.getUniqueOrderNumber() == null || orderEvent.getUniqueOrderNumber().isBlank()) {
            throw new IllegalArgumentException("uniqueOrderNumber must not be blank");
        }
        if (orderEvent.getTotalAmount() == null) {
            throw new IllegalArgumentException("totalAmount must not be null");
        }
        if (expectedStatus == null) {
            throw new IllegalArgumentException("expectedStatus must not be null");
        }
        if (orderEvent.getStatus() == null) {
            orderEvent.setStatus(expectedStatus);
            return orderEvent;
        }
        if (orderEvent.getStatus() != expectedStatus) {
            throw new IllegalArgumentException(
                    "Order status mismatch between payload and header: payload=%s, header=%s"
                            .formatted(orderEvent.getStatus(), expectedStatus)
            );
        }
        return orderEvent;
    }
}
