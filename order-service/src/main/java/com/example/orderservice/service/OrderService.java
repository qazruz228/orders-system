package com.example.orderservice.service;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.CreateOrderResponse;
import com.example.orderservice.entity.Product;
import com.example.orderservice.util.validator.ProductValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductService productService;
    private final ProductValidator productValidator;

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        Map<Long, Product> productsById = productValidator.validateCreateOrderRequest(request);
        request.setTotalAmount(productService.updateProductQuantitiesAndCalculateTotal(request, productsById));

        return CreateOrderResponse.builder()
                .message("Your order has been accepted, move to payment-service for payment")
                .totalAmount(request.getTotalAmount())
                .build();
    }
}
