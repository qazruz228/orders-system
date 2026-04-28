package com.example.orderservice.outbox.publisher;

import com.example.orderservice.events.CreateOrderEvent;
import com.example.orderservice.events.OutboxEvent;
import com.example.orderservice.events.enums.OutboxStatus;
import com.example.orderservice.repository.OutboxEventRepository;
import com.example.orderservice.util.converter.JsonConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxPublisher {

    private static final int INITIAL_RETRY_COUNT = 0;

    private final OutboxEventRepository outboxEventRepository;
    private final JsonConverter jsonConverter;

    @Transactional
    public void saveEvent(CreateOrderEvent createOrderEvent) {
        if (createOrderEvent == null) {
            throw new IllegalArgumentException("createOrderEvent must not be null");
        }

        String payload = jsonConverter.toJson(createOrderEvent);

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .payload(payload)
                .outboxStatus(OutboxStatus.NEW)
                .retryCount(INITIAL_RETRY_COUNT)
                .requestId(UUID.randomUUID())
                .build();

        OutboxEvent savedEvent = outboxEventRepository.save(outboxEvent);
        log.debug("Saved outbox event with id={} and status={}", savedEvent.getId(), savedEvent.getOutboxStatus());
    }
}
