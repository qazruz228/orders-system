package com.example.paymentservice.events;

import com.example.paymentservice.events.enums.OrderEventStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderEvent {

    private String deliveryAddress;

    private BigDecimal totalAmount;

    private String uniqueOrderNumber;

    private Long orderId;

    private OrderEventStatus status;

//    private List<OrderItemDto> orderItems;



}
