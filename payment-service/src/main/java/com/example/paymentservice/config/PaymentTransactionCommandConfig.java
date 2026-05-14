package com.example.paymentservice.config;


import com.example.paymentservice.events.enums.OrderEventStatus;
import com.example.paymentservice.service.handler.CancelOrderHandler;
import com.example.paymentservice.service.handler.CreateOrderHandler;
import com.example.paymentservice.service.handler.OrderHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class PaymentTransactionCommandConfig {

    @Bean
    public Map<OrderEventStatus, OrderHandler> commandHandlers(
            CreateOrderHandler createOrderHandler,
            CancelOrderHandler cancelOrderHandler
    ) {
        return Map.of(
                OrderEventStatus.CREATED, createOrderHandler,
                OrderEventStatus.CANCELLED, cancelOrderHandler
        );
    }
}
