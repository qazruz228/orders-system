package com.example.orderservice.util.validator;


import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.CreateOrderResponse;
import com.example.orderservice.dto.OrderItemDto;
import com.example.orderservice.entity.Product;
import com.example.orderservice.entity.enums.OrderStatus;
import com.example.orderservice.entity.enums.converter.OrderStatusConverter;
import com.example.orderservice.error.exception.OrderValidationException;
import com.example.orderservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductValidator {

    private final ProductRepository productRepository;

    public void validateCreateOrderRequest(CreateOrderRequest request) {

        List<String> errors = new ArrayList<>();

        for (OrderItemDto item : request.getOrderItems()) {

            Product product = productRepository.findById(item.getProductId())
                    .orElse(null);

            if (product == null) {
                errors.add("Product with id " + item.getProductId() + " not found");
                continue;
            }

            if (product.getQuantity() < item.getQuantity()) {
                errors.add("Not enough quantity for product id " + item.getProductId());
            }
        }

        if (!errors.isEmpty()) {
            throw new OrderValidationException(errors);
        }
    }
}



