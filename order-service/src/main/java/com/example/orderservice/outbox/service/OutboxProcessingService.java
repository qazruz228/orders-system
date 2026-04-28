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
        OutboxEvent event = outboxEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalStateException("Outbox event not found: " + eventId));

        event.setOutboxStatus(OutboxStatus.SENT);
        event.setProcessedAt(LocalDateTime.now());
        outboxEventRepository.save(event);
    }

    @Transactional
    public void handlePublishFailure(Long eventId, int maxRetry, Throwable throwable) {
        OutboxEvent event = outboxEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalStateException("Outbox event not found: " + eventId));

        int retry = (event.getRetryCount() == null ? 0 : event.getRetryCount()) + 1;
        event.setRetryCount(retry);
        event.setProcessedAt(LocalDateTime.now());
        event.setOutboxStatus(retry >= maxRetry ? OutboxStatus.FAILED : OutboxStatus.NEW);

        outboxEventRepository.save(event);
        log.warn(
                "Failed to publish outbox event id={} requestId={} retry={} status={}",
                event.getId(),
                event.getRequestId(),
                retry,
                event.getOutboxStatus(),
                throwable
        );
    }
}
