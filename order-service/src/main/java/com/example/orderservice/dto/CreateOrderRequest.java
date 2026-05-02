package com.example.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {

    @NotBlank(message = "address must not be empty")
    private String deliveryAddress;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal totalAmount;

    @Valid
    @NotEmpty(message = "order items must not be empty")
    private List<OrderItemDto> orderItems;



}
