package com.example.orderservice.events;

import com.example.orderservice.dto.OrderItemDto;
import com.example.orderservice.entity.enums.OrderEventStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
// РАЗОБРАТЬСЯ С APACHE AVRO
public class OrderEvent {

    private String deliveryAddress;

    private BigDecimal totalAmount;

    private String uniqueOrderNumber;

    private Long orderId;

    private OrderEventStatus status;

    private List<OrderItemDto> orderItems;



}
