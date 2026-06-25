package com.example.paymentservice.kafka.producer.outbox;

import com.example.paymentservice.events.PaymentProcessedEvent;
import com.example.paymentservice.events.outbox.OutboxEvent;
import com.example.paymentservice.events.outbox.OutboxStatus;
import com.example.paymentservice.repository.OutboxEventRepository;
import com.example.paymentservice.util.converter.JsonConverter;
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
    public void save(PaymentProcessedEvent paymentProcessedEvent) {
        if (paymentProcessedEvent == null) {
            throw new IllegalArgumentException("paymentProcessedEvent must not be null");
        }

        String payload = jsonConverter.toJson(paymentProcessedEvent);

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .payload(payload)
                .outboxStatus(OutboxStatus.NEW)
                .paymentStatus(paymentProcessedEvent.getPaymentStatus())
                .retryCount(INITIAL_RETRY_COUNT)
                .uniqueOrderNumber(paymentProcessedEvent.getUniqueOrderNumber())
                .orderId(paymentProcessedEvent.getOrderId())
                .build();

        OutboxEvent savedEvent = outboxEventRepository.save(outboxEvent);
        OutboxEvent eventToLog = savedEvent != null ? savedEvent : outboxEvent;
        log.info(
                "Payment outbox saved eventId={} orderId={} uniqueOrderNumber={} paymentStatus={} outboxStatus={} payloadBytes={}",
                eventToLog.getId(),
                eventToLog.getOrderId(),
                eventToLog.getUniqueOrderNumber(),
                eventToLog.getPaymentStatus(),
                eventToLog.getOutboxStatus(),
                payload.length()
        );
    }
}
