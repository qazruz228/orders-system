package com.example.paymentservice.service.handler;

import com.example.paymentservice.entity.Transaction;
import com.example.paymentservice.entity.enums.TransactionStatus;
import com.example.paymentservice.events.OrderEvent;
import com.example.paymentservice.events.ProcessedEvent;
import com.example.paymentservice.events.enums.OrderEventStatus;
import com.example.paymentservice.repository.ProcessedEventsRepository;
import com.example.paymentservice.repository.TransactionRepository;
import com.example.paymentservice.util.converter.ConverterEventToTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateOrderHandler implements OrderHandler {

    private final ConverterEventToTransaction converterEventToTransaction;
    private final TransactionRepository transactionRepository;
    private final ProcessedEventsRepository processedEventsRepository;

    @Override
    public boolean supports(OrderEventStatus orderStatus) {
        return orderStatus == OrderEventStatus.CREATED;
    }

    @Override
    public void process(OrderEvent orderEvent, String payload) {

        if (processedEventsRepository.existsByUniqueOrderNumberAndOrderStatus(orderEvent.getUniqueOrderNumber(), orderEvent.getStatus())) {
            return;
        }

        ProcessedEvent processedEvent = new ProcessedEvent();
        processedEvent.setUniqueOrderNumber(orderEvent.getUniqueOrderNumber());
        processedEvent.setOrderStatus(orderEvent.getStatus());
        processedEvent.setPayload(payload);
        processedEventsRepository.save(processedEvent);

        if (transactionRepository.findByUniqueOrderNumber(orderEvent.getUniqueOrderNumber()).isPresent()) {
            return;
        }

        Transaction transaction = converterEventToTransaction.convert(orderEvent);
        transaction.setStatus(TransactionStatus.PENDING);
        transactionRepository.save(transaction);
    }

}
