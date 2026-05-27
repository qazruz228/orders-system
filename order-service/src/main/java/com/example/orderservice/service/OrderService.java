package com.example.orderservice.service;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.CreateOrderResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.Product;
import com.example.orderservice.entity.enums.OrderEventStatus;
import com.example.orderservice.events.OrderEvent;
import com.example.orderservice.util.converter.OrderRequestConverterToEvent;
import com.example.orderservice.mapper.OrderMapper;
import com.example.orderservice.kafka.producer.outbox.publisher.OutboxPublisher;
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
             "remember your unique_order_number because it need you for payment, " +
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
        String uniqOrderNumber = UUID.randomUUID().toString();

        request.setTotalAmount(totalAmount);

        Order order = orderMapper.toOrder(request);
        order.setUniqueOrderNumber(uniqOrderNumber);
        order.setStatus(OrderEventStatus.CREATED);
        Order savedOrder = orderRepository.save(order);
        OrderEvent orderEvent = requestConverter.convert(request);
        orderEvent.setUniqueOrderNumber(uniqOrderNumber);
        orderEvent.setOrderId(savedOrder.getId());
        outboxPublisher.saveEvent(orderEvent);

        log.info("Created order id={} uniqOrderNumber={} items={}", savedOrder.getId(), uniqOrderNumber, request.getOrderItems().size());

        return CreateOrderResponse.builder()
                .message(ORDER_CREATED_MESSAGE)
                .uniqueOrderNumber(uniqOrderNumber)
                .build();
    }

    @Transactional
    public void cancelOrder(String uniqueOrderNumber) {
        if (uniqueOrderNumber == null || uniqueOrderNumber.isBlank()) {
            throw new IllegalArgumentException("uniqueOrderNumber must not be blank");
        }

        Order order = orderRepository.findByUniqueOrderNumberForUpdate(uniqueOrderNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found for uniqueOrderNumber=" + uniqueOrderNumber
                ));

        if (order.getStatus() == OrderEventStatus.CANCELLED) {
            return;
        }
        if (order.getStatus() == OrderEventStatus.COMPLETED) {
            throw new IllegalStateException("Completed order cannot be cancelled");
        }


        OrderEvent orderEvent = OrderEvent.builder()
                .orderId(order.getId())
                .uniqueOrderNumber(order.getUniqueOrderNumber())
                .deliveryAddress(order.getDeliveryAddress())
                .totalAmount(order.getTotalAmount())
                .status(OrderEventStatus.CANCELLED)
                .build();

        outboxPublisher.saveEvent(orderEvent);
    }

}
