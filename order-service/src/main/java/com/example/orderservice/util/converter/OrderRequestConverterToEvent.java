package com.example.orderservice.util.converter;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.entity.enums.OrderEventStatus;
import com.example.orderservice.events.CreateOrderEvent;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class OrderRequestConverterToEvent implements Converter<CreateOrderRequest, CreateOrderEvent>{


    @Override
    public CreateOrderEvent convert(CreateOrderRequest request) {
        return CreateOrderEvent.builder()
                .deliveryAddress(request.getDeliveryAddress())
                .totalAmount(request.getTotalAmount())
                .status(OrderEventStatus.PENDING)
                .orderItems(request.getOrderItems())
                .build();
    }
}

