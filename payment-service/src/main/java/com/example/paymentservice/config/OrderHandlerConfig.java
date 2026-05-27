package com.example.paymentservice.config;

import com.example.paymentservice.events.enums.OrderEventStatus;
import com.example.paymentservice.service.handler.OrderHandler;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderHandlerConfig {

    private final Map<OrderEventStatus, OrderHandler> handlersByStatus;

    public OrderHandlerConfig(List<OrderHandler> handlers) {
        this.handlersByStatus = new EnumMap<>(OrderEventStatus.class);

        for (OrderHandler handler : handlers) {
            for (OrderEventStatus status : OrderEventStatus.values()) {
                if (!handler.supports(status)) {
                    continue;
                }
                OrderHandler previous = handlersByStatus.putIfAbsent(status, handler);
                if (previous != null) {
                    throw new IllegalStateException("Multiple handlers registered for status " + status);
                }
            }
        }
    }

    public OrderHandler getHandler(OrderEventStatus orderStatus) {
        OrderHandler handler = handlersByStatus.get(orderStatus);
        if (handler == null) {
            throw new IllegalArgumentException("No handler registered for status " + orderStatus);
        }
        return handler;
    }
}

