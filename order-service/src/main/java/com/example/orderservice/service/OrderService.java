package com.example.orderservice.service;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.CreateOrderResponse;
import com.example.orderservice.dto.OrderItemDto;
import com.example.orderservice.util.validator.ProductValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductService productService;
    private final ProductValidator productValidator;

    @Transactional
    public CreateOrderResponse createOrder(String deliveryAddress, Long productId, Integer quantity) {
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setProductId(productId);
        orderItemDto.setQuantity(quantity);

        List<OrderItemDto> orderItems = List.of(orderItemDto);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setDeliveryAddress(deliveryAddress);
        request.setOrderItems(orderItems);

        productValidator.validateCreateOrderRequest(request);

        productService.updateProduct(request);

        CreateOrderResponse response = new CreateOrderResponse();

        response.setUniqueOrderId(UUID.randomUUID());
        response.setMessage("Your order has been accepted, copy your ID because you will need it for payment");

        return response;
    }
}

