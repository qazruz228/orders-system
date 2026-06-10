package com.example.orderservice.util.validator;

import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.enums.OrderEventStatus;
import com.example.orderservice.events.PaymentProcessedEvent;
import com.example.orderservice.events.enums.PaymentStatus;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentValidator {

    private final OrderRepository orderRepository;

    public Order validatePaymentAndGetOrder(PaymentProcessedEvent paymentEvent) {
        if (paymentEvent == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        if (paymentEvent.getUniqueOrderNumber() == null || paymentEvent.getUniqueOrderNumber().isBlank()) {
            throw new IllegalArgumentException("uniqueOrderNumber must not be blank");
        }
        if (paymentEvent.getPaymentStatus() == null) {
            throw new IllegalArgumentException("paymentStatus must not be null");
        }

        Order order = orderRepository.findByUniqueOrderNumberForUpdate(paymentEvent.getUniqueOrderNumber())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found for uniqueOrderNumber=" + paymentEvent.getUniqueOrderNumber())
                );

        if (order.getStatus() == OrderEventStatus.CANCELLED && paymentEvent.getPaymentStatus() == PaymentStatus.SUCCEEDED) {
            throw new IllegalStateException("Cancelled order cannot transition to COMPLETED");
        }

        return order;

    }


}



