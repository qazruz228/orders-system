package com.example.paymentservice.service.handler;

import com.example.paymentservice.events.OrderEvent;

public interface OrderHandler {
    void process(OrderEvent orderEvent);
}
