package com.example.orderservice.events;

import com.example.orderservice.events.enums.OutboxStatus;
import com.example.orderservice.events.enums.converter.OutboxStatusConverter;
import jakarta.persistence.*;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Convert(converter = OutboxStatusConverter.class)
    @Column(name = "outbox_status", nullable = false, length = 50)
    private OutboxStatus outboxStatus;

    @Column(name = "retry_count")
    private Integer retryCount;

    @Column(name = "unique_order_number", nullable = false)
    private String uniqueOrderNumber;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;
}
