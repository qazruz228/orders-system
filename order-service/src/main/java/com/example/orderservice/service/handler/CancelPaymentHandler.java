package com.example.orderservice.service.handler;

import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.enums.OrderEventStatus;
import com.example.orderservice.events.PaymentProcessedEvent;
import com.example.orderservice.events.enums.PaymentStatus;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.util.validator.PaymentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class CancelPaymentHandler implements PaymentHandler {

    private final PaymentValidator paymentValidator;
    private final OrderRepository orderRepository;


    @Override
    public boolean supports(PaymentStatus paymentStatus) {
        return paymentStatus == PaymentStatus.CANCELLED;
    }

    @Override
    @Transactional
    public void process(PaymentProcessedEvent paymentEvent) {

        Order order = paymentValidator.validatePaymentAndGetOrder(paymentEvent);

        order.setStatus(OrderEventStatus.CANCELLED);

        Order savedOrder = orderRepository.save(order);
        log.info(
                "Order payment applied orderId={} uniqueOrderNumber={} paymentStatus={} orderStatus={}",
                savedOrder.getId(),
                savedOrder.getUniqueOrderNumber(),
                paymentEvent.getPaymentStatus(),
                savedOrder.getStatus()
        );
    }


}
