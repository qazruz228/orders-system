package com.example.paymentservice.events;

import com.example.paymentservice.events.enums.OrderEventStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class OrderEvent {

    private String deliveryAddress;

    private BigDecimal totalAmount;

    private String uniqueOrderNumber;

    private Long orderId;

    private OrderEventStatus status;

//    private List<OrderItemDto> orderItems;



}
