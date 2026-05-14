package com.example.orderservice.util.converter;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.entity.enums.OrderEventStatus;
import com.example.orderservice.events.OrderEvent;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class OrderRequestConverterToEvent implements Converter<CreateOrderRequest, OrderEvent>{


    @Override
    public OrderEvent convert(CreateOrderRequest request) {
        return OrderEvent.builder()
                .deliveryAddress(request.getDeliveryAddress())
                .totalAmount(request.getTotalAmount())
                .status(OrderEventStatus.PENDING)
                .orderItems(request.getOrderItems())
                .build();
    }
}

