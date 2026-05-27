package com.example.orderservice.service.handler;

import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.enums.OrderEventStatus;
import com.example.orderservice.events.PaymentProcessedEvent;
import com.example.orderservice.events.enums.PaymentStatus;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.util.validator.PaymentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancelPaymentHandler implements PaymentHandler {

    private final PaymentValidator paymentValidator;
    private final OrderRepository orderRepository;


    @Override
    public boolean supports(PaymentStatus paymentStatus) {
        return paymentStatus == PaymentStatus.CANCELLED;
    }

    @Override
    public void process(PaymentProcessedEvent paymentEvent) {

        Order order = paymentValidator.validatePaymentAndGetOrder(paymentEvent);

        order.setStatus(OrderEventStatus.CANCELLED);

        orderRepository.save(order);
    }


}
