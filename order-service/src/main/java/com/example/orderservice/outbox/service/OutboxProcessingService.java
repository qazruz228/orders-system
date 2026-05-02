package com.example.orderservice.outbox.service;

import com.example.orderservice.events.OutboxEvent;
import com.example.orderservice.events.enums.OutboxStatus;
import com.example.orderservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxProcessingService {

    private final OutboxEventRepository outboxEventRepository;

    @Transactional
    public List<OutboxEvent> claimNextBatch(int batchSize, Duration processingTimeout) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime staleThreshold = now.minus(processingTimeout);

        List<OutboxEvent> events = outboxEventRepository.lockNextBatch(batchSize, staleThreshold);
        for (OutboxEvent event : events) {
            event.setOutboxStatus(OutboxStatus.PROCESSING);
            event.setProcessedAt(now);
        }

        return outboxEventRepository.saveAll(events);
    }

    @Transactional
    public void markSent(Long eventId) {
        int updated = outboxEventRepository.updateStatus(eventId, OutboxStatus.SENT, LocalDateTime.now());
        if (updated == 0) {
            throw new IllegalStateException("Outbox event not found: " + eventId);
        }
    }

    @Transactional
    public void handlePublishFailure(Long eventId, UUID requestId, Integer currentRetryCount, int maxRetry, Throwable throwable) {
        LocalDateTime now = LocalDateTime.now();
        int updated = outboxEventRepository.recordPublishFailure(
                eventId,
                maxRetry,
                OutboxStatus.FAILED,
                OutboxStatus.NEW,
                now
        );
        if (updated == 0) {
            throw new IllegalStateException("Outbox event not found: " + eventId);
        }

        int nextRetry = (currentRetryCount == null ? 0 : currentRetryCount) + 1;

        log.warn(
                "Failed to publish outbox event id={} requestId={} retry={} nextStatus={}",
                eventId,
                requestId,
                nextRetry,
                nextRetry >= maxRetry ? OutboxStatus.FAILED : OutboxStatus.NEW,
                throwable
        );
    }
}
