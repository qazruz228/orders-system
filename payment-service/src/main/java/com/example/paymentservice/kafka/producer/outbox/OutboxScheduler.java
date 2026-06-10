package com.example.paymentservice.kafka.producer.outbox;

import com.example.paymentservice.config.KafkaTopicProperties;
import com.example.paymentservice.events.outbox.OutboxEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "outbox.enabled", havingValue = "true", matchIfMissing = true)
public class OutboxScheduler {

    private static final String PAYMENT_STATUS_HEADER = "paymentStatus";

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

        log.info("Payment outbox claimed count={} topic={}", events.size(), kafkaTopicProperties.getName());
        publishSequentially(events, 0);
    }

    private void publishSequentially(List<OutboxEvent> events, int index) {
        if (index >= events.size()) {
            return;
        }

        OutboxEvent event = events.get(index);
        ProducerRecord<String, String> record =
                new ProducerRecord<>(kafkaTopicProperties.getName(), event.getUniqueOrderNumber(), event.getPayload());

        record.headers().add(
                PAYMENT_STATUS_HEADER,
                event.getPaymentStatus().name().getBytes(StandardCharsets.UTF_8)
        );
        log.info(
                "Payment event publish requested eventId={} orderId={} uniqueOrderNumber={} paymentStatus={} topic={} key={}",
                event.getId(),
                event.getOrderId(),
                event.getUniqueOrderNumber(),
                event.getPaymentStatus(),
                kafkaTopicProperties.getName(),
                event.getUniqueOrderNumber()
        );

        kafkaTemplate.send(record)
                .whenComplete((result, throwable) -> {
                    try {
                        if (throwable == null) {
                            outboxProcessingService.markSent(event.getId());
                            RecordMetadata metadata = result.getRecordMetadata();
                            log.info(
                                    "Payment event published eventId={} orderId={} uniqueOrderNumber={} paymentStatus={} topic={} partition={} offset={}",
                                    event.getId(),
                                    event.getOrderId(),
                                    event.getUniqueOrderNumber(),
                                    event.getPaymentStatus(),
                                    metadata.topic(),
                                    metadata.partition(),
                                    metadata.offset()
                            );
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
                        log.error("Payment outbox callback handling failed eventId={}", event.getId(), callbackException);
                    } finally {
                        publishSequentially(events, index + 1);
                    }
                });
    }
}
