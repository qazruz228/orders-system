package com.example.orderservice.config;

import com.example.orderservice.events.PaymentProcessedEvent;
import com.example.orderservice.events.enums.PaymentStatus;
import com.example.orderservice.service.handler.PaymentHandler;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class PaymentHandlerConfig {

    private final Map<PaymentStatus, PaymentHandler> handlersByStatus;

    public PaymentHandlerConfig(List<PaymentHandler> handlers) {
        this.handlersByStatus = new EnumMap<>(PaymentStatus.class);

        for (PaymentHandler handler : handlers) {
            for (PaymentStatus status : PaymentStatus.values()) {
                if (!handler.supports(status)) {
                    continue;
                }
                PaymentHandler previous = handlersByStatus.putIfAbsent(status, handler);
                if (previous != null) {
                    throw new IllegalArgumentException("Multiple handlers registered for status " + status);
                }
            }
        }
    }


    public PaymentHandler getHandler(PaymentStatus paymentStatus) {
        PaymentHandler handler = handlersByStatus.get(paymentStatus);
        if (handler == null) {
            throw new IllegalArgumentException("No handler registered by status " + paymentStatus);
        }
        return handler;
    }

}
