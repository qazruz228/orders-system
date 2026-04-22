package com.example.orderservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateOrderResponse {

public UUID uniqueOrderId;

public String message;


}
