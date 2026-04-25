package com.example.orderservice.util.validator;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderItemDto;
import com.example.orderservice.entity.Product;
import com.example.orderservice.error.exception.OrderValidationException;
import com.example.orderservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductValidator {

    private final ProductRepository productRepository;

    public Map<Long, Product> validateCreateOrderRequest(CreateOrderRequest request) {

        List<String> errors = new ArrayList<>();
        List<OrderItemDto> orderItems = request.getOrderItems();

        if (orderItems == null || orderItems.isEmpty()) {
            throw new OrderValidationException(List.of("Order items must not be empty"));
        }

        Map<Long, Integer> requestedQuantities = new HashMap<>();

        for (int i = 0; i < orderItems.size(); i++) {
            OrderItemDto item = orderItems.get(i);

            if (item == null) {
                errors.add("Order item at index " + i + " must not be null");
                continue;
            }

            Long productId = item.getProductId();
            Integer quantity = item.getQuantity();

            if (productId == null) {
                errors.add("Product id must not be null for item at index " + i);
                continue;
            }

            if (quantity == null || quantity <= 0) {
                errors.add("Quantity must be greater than zero for product id " + productId);
                continue;
            }

            requestedQuantities.merge(productId, quantity, Integer::sum);
        }

        if (!errors.isEmpty()) {
            throw new OrderValidationException(errors);
        }

        Map<Long, Product> productsById = productRepository
                .findAllById(requestedQuantities.keySet())
                .stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        for (Long productId : requestedQuantities.keySet()) {
            if (!productsById.containsKey(productId)) {
                errors.add("Product with id " + productId + " not found");
            }
        }

        for (Map.Entry<Long, Integer> entry : requestedQuantities.entrySet()) {
            Long productId = entry.getKey();
            Integer requestedQty = entry.getValue();

            Product product = productsById.get(productId);
            if (product != null && product.getQuantity() < requestedQty) {
                errors.add("Not enough quantity for product id " + productId);
            }
        }

        if (!errors.isEmpty()) {
            log.debug("Order validation failed: {}", errors);
            throw new OrderValidationException(errors);
        }

        return productsById;
    }
}
