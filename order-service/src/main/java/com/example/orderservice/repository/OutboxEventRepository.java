package com.example.orderservice.repository;

import com.example.orderservice.events.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
