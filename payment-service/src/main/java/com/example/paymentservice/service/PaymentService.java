package com.example.paymentservice.service;

import com.example.paymentservice.dto.RemitPaymentRequest;
import com.example.paymentservice.dto.RemitPaymentResponse;
import com.example.paymentservice.entity.ProcessedEvent;
import com.example.paymentservice.entity.Transaction;
import com.example.paymentservice.entity.enums.TransactionStatus;
import com.example.paymentservice.events.OrderEvent;
import com.example.paymentservice.events.enums.OrderEventStatus;
import com.example.paymentservice.repository.ProcessedEventsRepository;
import com.example.paymentservice.repository.TransactionRepository;
import com.example.paymentservice.service.handler.OrderHandler;
import com.example.paymentservice.util.validator.EventValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ProcessedEventsRepository processedEventsRepository;
    private final TransactionRepository transactionRepository;
    private final EventValidator eventValidator;
    private final OrderHandler orderHandler;

    @Transactional
    public void processIncomingEvent(OrderEventStatus orderStatus, String payload) {
        OrderEvent orderEvent = eventValidator.validate(payload);

        orderHandler.process(orderEvent);
    }

    @Transactional
    public RemitPaymentResponse remitPayment(RemitPaymentRequest request) {


        if (request == null || request.getUniqueOrderNumber() == null || request.getUniqueOrderNumber().isBlank()) {
            throw new IllegalArgumentException("uniqueOrderNumber must not be blank");
        }
        ProcessedEvent processedEvent = processedEventsRepository.existsByUniqueOrderNumber(request);
        processedEvent.setOrderStatus(OrderEventStatus.valueOf("COMPLETED"));


        markTransactionStatus(request.getUniqueOrderNumber());


        RemitPaymentResponse response = new RemitPaymentResponse();
        response.setMessage("Transaction successful processed");

        return response;
    }


    public void markTransactionStatus(String uniqueOrderNumber) {

        Transaction transaction = transactionRepository.findByUniqueOrderNumber(uniqueOrderNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Transaction not found for uniqeOrderNumber=" + uniqueOrderNumber));

        if (transaction.getStatus() == TransactionStatus.PENDING) {
            transaction.setStatus(TransactionStatus.COMPLETED);
        }
        if (transaction.getStatus() == TransactionStatus.CANCELLED) {
            transaction.setStatus(TransactionStatus.CANCELLED);
        }

        transactionRepository.save(transaction);

    }


}
