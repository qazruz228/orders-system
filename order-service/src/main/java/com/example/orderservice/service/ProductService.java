package com.example.orderservice.service;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderItemDto;
import com.example.orderservice.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@Slf4j
public class ProductService {

    public BigDecimal updateProductQuantitiesAndCalculateTotal(
            CreateOrderRequest orderRequest,
            Map<Long, Product> productsById
    ) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemDto item : orderRequest.getOrderItems()) {
            Product product = productsById.get(item.getProductId());
            product.setQuantity(product.getQuantity() - item.getQuantity());
            totalAmount = totalAmount.add(
                    product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            );
        }

        log.debug("Updated product quantities for {} order items", orderRequest.getOrderItems().size());
        return totalAmount;
    }
}
