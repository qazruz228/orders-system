package com.example.orderservice.kafka.producer.outbox.publisher;

import com.example.orderservice.entity.enums.OrderEventStatus;
import com.example.orderservice.events.OrderEvent;
import com.example.orderservice.events.OutboxEvent;
import com.example.orderservice.events.enums.OutboxStatus;
import com.example.orderservice.repository.OutboxEventRepository;
import com.example.orderservice.util.converter.JsonConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxPublisher {

    private static final int INITIAL_RETRY_COUNT = 0;

    private final OutboxEventRepository outboxEventRepository;
    private final JsonConverter jsonConverter;

    @Transactional
    public void saveEvent(OrderEvent orderEvent) {
        if (orderEvent == null) {
            throw new IllegalArgumentException("orderEvent must not be null");
        }

        String payload = jsonConverter.toJson(orderEvent);

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .payload(payload)
                .uniqueOrderNumber(orderEvent.getUniqueOrderNumber())
                .orderId(orderEvent.getOrderId())
                .outboxStatus(OutboxStatus.NEW)
                .retryCount(INITIAL_RETRY_COUNT)
                .orderStatus(OrderEventStatus.CREATED)
                .build();

        OutboxEvent savedEvent = outboxEventRepository.save(outboxEvent);
        log.debug("Saved outbox event with id={} and status={}", savedEvent.getId(), savedEvent.getOutboxStatus());
    }
}
