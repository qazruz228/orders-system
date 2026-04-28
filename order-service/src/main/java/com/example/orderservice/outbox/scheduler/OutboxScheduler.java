package com.example.orderservice.outbox.scheduler;

import com.example.orderservice.config.KafkaTopicProperties;
import com.example.orderservice.events.OutboxEvent;
import com.example.orderservice.outbox.service.OutboxProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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

    private final AtomicBoolean batchInProgress = new AtomicBoolean(false);

    @Scheduled(fixedDelayString = "${outbox.polling.interval:30000}")
    public void processOutbox() {
        if (!batchInProgress.compareAndSet(false, true)) {
            log.debug("Previous outbox batch is still in progress, skip current schedule tick");
            return;
        }

        List<OutboxEvent> events = outboxProcessingService.claimNextBatch(batchSize, processingTimeout);
        if (events.isEmpty()) {
            batchInProgress.set(false);
            return;
        }

        log.debug("Claimed {} outbox event(s) for topic={}", events.size(), kafkaTopicProperties.getName());
        publishSequentially(events, 0);
    }

    private void publishSequentially(List<OutboxEvent> events, int index) {
        if (index >= events.size()) {
            batchInProgress.set(false);
            return;
        }

        OutboxEvent event = events.get(index);
        kafkaTemplate.send(
                kafkaTopicProperties.getName(),
                String.valueOf(event.getRequestId()),
                event.getPayload()
        ).whenComplete((result, throwable) -> {
            try {
                if (throwable == null) {
                    outboxProcessingService.markSent(event.getId());
                    log.debug("Outbox event id={} sent to topic={}", event.getId(), kafkaTopicProperties.getName());
                } else {
                    outboxProcessingService.handlePublishFailure(event.getId(), maxRetry, throwable);
                }
            } catch (Exception callbackException) {
                log.error("Outbox callback handling failed for event id={}", event.getId(), callbackException);
            } finally {
                publishSequentially(events, index + 1);
            }
        });
    }
}
