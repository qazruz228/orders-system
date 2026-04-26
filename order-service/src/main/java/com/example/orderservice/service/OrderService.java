package com.example.orderservice.service;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.CreateOrderResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.Product;
import com.example.orderservice.mapper.OrderMapper;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.util.validator.ProductValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductService productService;
    private final ProductValidator productValidator;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        Map<Long, Product> productsById = productValidator.validateCreateOrderRequest(request);
        request.setTotalAmount(productService.updateProductQuantitiesAndCalculateTotal(request, productsById));
        request.setUniqId(Long.valueOf(UUID.randomUUID().toString()));

        Order order = orderMapper.toOrder(request);
        orderRepository.save(order);




        return CreateOrderResponse.builder()
                .message("Your order has been accepted, " +
                        "remember your uniqId because it need you for payment, " +
                        "move to payment-service for payment")
                .uniqId(request.getUniqId())
                .totalAmount(request.getTotalAmount())
                .build();
    }



//    public CancelOrderResponse cancelOrder(CancelOrderRequest request){
//
//
//
//    }


}
