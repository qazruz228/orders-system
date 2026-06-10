package com.example.orderservice.service;

import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.enums.OrderEventStatus;
import com.example.orderservice.events.PaymentProcessedEvent;
import com.example.orderservice.events.enums.PaymentStatus;
import com.example.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderStatusServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderStatusService orderStatusService;

    @Test
    void shouldApplySucceededPaymentStatus() {
        Order order = new Order();
        order.setStatus(OrderEventStatus.CREATED);
        order.setUniqueOrderNumber("order-10");

        PaymentProcessedEvent event = new PaymentProcessedEvent();
        event.setUniqueOrderNumber("order-10");
        event.setPaymentStatus(PaymentStatus.SUCCEEDED);

        when(orderRepository.findByUniqueOrderNumberForUpdate("order-10")).thenReturn(Optional.of(order));

        orderStatusService.applyPaymentEvent(event);

        assertEquals(OrderEventStatus.COMPLETED, order.getStatus());
        verify(orderRepository).save(order);
    }
}
