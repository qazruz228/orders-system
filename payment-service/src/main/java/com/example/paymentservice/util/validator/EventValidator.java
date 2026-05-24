package com.example.paymentservice.util.validator;

import com.example.paymentservice.events.OrderEvent;
import com.example.paymentservice.util.converter.JsonConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventValidator {

    private final JsonConverter jsonConverter;

    public OrderEvent validate(String value) {
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
        return orderEvent;
    }
}
