package com.example.paymentservice.repository;

import com.example.paymentservice.events.ProcessedEvent;
import com.example.paymentservice.events.enums.OrderEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProcessedEventsRepository extends JpaRepository<ProcessedEvent, Long> {
    boolean existsByUniqueOrderNumberAndOrderStatus(String uniqueOrderNumber, OrderEventStatus orderStatus);

    Optional<ProcessedEvent> findTopByUniqueOrderNumberAndOrderStatus(String uniqueOrderNumber, OrderEventStatus orderStatus);
}
