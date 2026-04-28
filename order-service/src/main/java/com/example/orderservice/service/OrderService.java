package com.example.orderservice.service;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.CreateOrderResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.Product;
import com.example.orderservice.events.CreateOrderEvent;
import com.example.orderservice.util.converter.OrderRequestConverterToEvent;
import com.example.orderservice.mapper.OrderMapper;
import com.example.orderservice.outbox.publisher.OutboxPublisher;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.util.validator.ProductValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private static final String ORDER_CREATED_MESSAGE =
            "Your order has been accepted, " +
             "remember your uniqId because it need you for payment, " +
             "move to payment-service for payment";

    private final ProductService productService;
    private final ProductValidator productValidator;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OutboxPublisher outboxPublisher;
    private final OrderRequestConverterToEvent requestConverter;

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        Map<Long, Product> productsById = productValidator.validateCreateOrderRequest(request);
        BigDecimal totalAmount = productService.updateProductQuantitiesAndCalculateTotal(request, productsById);
        UUID uniqId = UUID.randomUUID();

        request.setTotalAmount(totalAmount);
        request.setUniqId(uniqId);

        Order savedOrder = orderRepository.save(orderMapper.toOrder(request));
        CreateOrderEvent createOrderEvent = requestConverter.convert(request);
        outboxPublisher.saveEvent(createOrderEvent);

        log.info("Created order id={} uniqId={} items={}", savedOrder.getId(), uniqId, request.getOrderItems().size());

        return CreateOrderResponse.builder()
                .message(ORDER_CREATED_MESSAGE)
                .uniqId(uniqId)
                .build();
    }

}
