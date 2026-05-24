package com.example.paymentservice.repository;

import com.example.paymentservice.dto.RemitPaymentRequest;
import com.example.paymentservice.entity.ProcessedEvent;
import com.example.paymentservice.events.enums.OrderEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventsRepository extends JpaRepository<ProcessedEvent, Long> {
    boolean existsByUniqueOrderNumberAndOrderStatus(String uniqueOrderNumber, OrderEventStatus orderStatus);

    ProcessedEvent existsByUniqueOrderNumber(RemitPaymentRequest request);
}
