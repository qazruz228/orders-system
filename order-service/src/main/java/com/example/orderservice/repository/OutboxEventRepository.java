package com.example.orderservice.repository;

import com.example.orderservice.events.OutboxEvent;
import com.example.orderservice.events.enums.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    @Query(
            value = """
                SELECT *
                FROM outbox_events
                WHERE outbox_status = 'NEW'
                   OR (outbox_status = 'PROCESSING' AND processed_at < :staleThreshold)
                ORDER BY created_at ASC
                LIMIT :batchSize
                FOR UPDATE SKIP LOCKED
            """,
            nativeQuery = true
    )
    List<OutboxEvent> lockNextBatch(@Param("batchSize") int batchSize,
                                    @Param("staleThreshold") LocalDateTime staleThreshold);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE OutboxEvent event
            SET event.outboxStatus = :status,
                event.processedAt = :processedAt
            WHERE event.id = :eventId
            """)
    int updateStatus(@Param("eventId") Long eventId,
                     @Param("status") OutboxStatus status,
                     @Param("processedAt") LocalDateTime processedAt);



    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE OutboxEvent event
            SET event.retryCount = COALESCE(event.retryCount, 0) + 1,
                event.processedAt = :processedAt,
                event.outboxStatus = CASE
                    WHEN COALESCE(event.retryCount, 0) + 1 >= :maxRetry THEN :failedStatus
                    ELSE :newStatus
                END
            WHERE event.id = :eventId
            """)
    int recordPublishFailure(@Param("eventId") Long eventId,
                             @Param("maxRetry") int maxRetry,
                             @Param("failedStatus") OutboxStatus failedStatus,
                             @Param("newStatus") OutboxStatus newStatus,
                             @Param("processedAt") LocalDateTime processedAt);
}
