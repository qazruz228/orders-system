package com.example.paymentservice.repository;

import com.example.paymentservice.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventsRepository extends JpaRepository<ProcessedEvent, Long> {


    Boolean existsByUniqueOrderNumber(String uniqueOrderNumber);


}
