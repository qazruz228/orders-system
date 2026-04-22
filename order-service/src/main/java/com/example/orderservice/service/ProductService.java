package com.example.orderservice.service;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderItemDto;
import com.example.orderservice.entity.Product;
import com.example.orderservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public CreateOrderRequest updateProduct(CreateOrderRequest orderRequest) {

        for (OrderItemDto item : orderRequest.getOrderItems()) {

            Product product = new Product();
            productRepository.findById(item.getProductId());

            product.setQuantity(product.getQuantity() - item.getQuantity());

            BigDecimal totalAmount = product.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            orderRequest.setTotalAmount(totalAmount);
        }

        return orderRequest;

    }
}





