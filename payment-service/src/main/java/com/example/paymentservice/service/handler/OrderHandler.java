package com.example.paymentservice.service.handler;

import com.example.paymentservice.events.enums.OrderEventStatus;
import com.example.paymentservice.events.OrderEvent;

public interface OrderHandler {
    boolean supports(OrderEventStatus orderStatus);

    void process(OrderEvent orderEvent, String payload);
}
