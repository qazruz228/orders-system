package com.example.orderservice.kafka.producer.outbox.scheduler;

import com.example.orderservice.config.KafkaTopicProperties;
import com.example.orderservice.entity.enums.OrderEventStatus;
import com.example.orderservice.events.OutboxEvent;
import com.example.orderservice.kafka.producer.outbox.service.OutboxProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTopicProperties kafkaTopicProperties;
    private final OutboxProcessingService outboxProcessingService;

    @Value("${outbox.batch-size:5}")
    private int batchSize;

    @Value("${outbox.max-retry:5}")
    private int maxRetry;

    @Value("${outbox.processing-timeout:PT2M}")
    private Duration processingTimeout;

    @Scheduled(fixedDelayString = "${outbox.polling.interval:30000}")
    public void processOutbox() {
        List<OutboxEvent> events = outboxProcessingService.claimNextBatch(batchSize, processingTimeout);
        if (events.isEmpty()) {
            return;
        }

        log.debug("Claimed {} outbox event(s) for topic={}", events.size(), kafkaTopicProperties.getName());
        publishSequentially(events, 0);
    }

    private void publishSequentially(List<OutboxEvent> events, int index) {
        if (index >= events.size()) {
            return;
        }

        OutboxEvent event = events.get(index);
        OrderEventStatus status = event.getOrderStatus();
        String orderStatus = status.toString();
        String orderIdKey = String.valueOf(event.getOrderId());
        ProducerRecord<String, String> record =
                new ProducerRecord<>(kafkaTopicProperties.getName(), orderIdKey, event.getPayload());

        record.headers().add("orderStatus", orderStatus.getBytes(StandardCharsets.UTF_8));

        kafkaTemplate.send(record)
                .whenComplete((result, throwable) -> {
                    try {
                        if (throwable == null) {
                            outboxProcessingService.markSent(event.getId());
                            log.debug("Outbox event id={} sent to topic={}", event.getId(), kafkaTopicProperties.getName());
                        } else {
                            outboxProcessingService.handlePublishFailure(
                                    event.getId(),
                                    event.getUniqueOrderNumber(),
                                    event.getRetryCount(),
                                    maxRetry,
                                    throwable
                            );
                        }
                    } catch (Exception callbackException) {
                        log.error("Outbox callback handling failed for event id={}", event.getId(), callbackException);
                    } finally {
                        publishSequentially(events, index + 1);
                    }
                });
    }
}
