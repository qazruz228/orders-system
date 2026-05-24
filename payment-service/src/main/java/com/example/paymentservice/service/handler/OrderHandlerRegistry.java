package com.example.paymentservice.service.handler;

import com.example.paymentservice.events.enums.OrderEventStatus;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderHandlerRegistry {

    private final Map<OrderEventStatus, OrderHandler> handlersByStatus;

    public OrderHandlerRegistry(List<OrderHandler> handlers) {
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

