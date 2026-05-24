package com.example.paymentservice.service.handler;

import com.example.paymentservice.events.OrderEvent;
import com.example.paymentservice.events.enums.OrderEventStatus;
import org.springframework.stereotype.Service;

@Service
public class CancelOrderHandler implements OrderHandler {

    @Override
    public boolean supports(OrderEventStatus orderStatus) {
        return orderStatus == OrderEventStatus.CANCELLED;
    }

    @Override
    public void process(OrderEvent orderEvent, String payload) {
        throw new UnsupportedOperationException("Cancel flow is not implemented yet");
    }
}
