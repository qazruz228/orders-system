package com.example.orderservice.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemDto {

    @NotNull(message = "productId must not be null")
    public Long productId;

    @NotNull(message = "quntity must not be null")
    public Integer quantity;

}
