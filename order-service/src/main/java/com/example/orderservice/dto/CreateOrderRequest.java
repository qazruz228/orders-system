package com.example.orderservice.dto;

import com.example.orderservice.entity.enums.OrderStatus;
import com.example.orderservice.entity.enums.converter.OrderStatusConverter;
import jakarta.persistence.Convert;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderRequest {

    public OrderStatus status;

    @NotBlank(message = "address must not be empty")
    public String deliveryAddress;

    private BigDecimal totalAmount;

    @NotNull(message = "order items must not be empty")
    public List<OrderItemDto> orderItems;

}
