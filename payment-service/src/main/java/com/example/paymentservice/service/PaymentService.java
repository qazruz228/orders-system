package com.example.paymentservice.service;

import com.example.paymentservice.dto.RemitPaymentRequest;
import com.example.paymentservice.dto.RemitPaymentResponse;
import com.example.paymentservice.entity.Transaction;
import com.example.paymentservice.entity.enums.TransactionStatus;
import com.example.paymentservice.events.OrderEvent;
import com.example.paymentservice.events.PaymentProcessedEvent;
import com.example.paymentservice.events.enums.OrderEventStatus;
import com.example.paymentservice.events.enums.PaymentStatus;
import com.example.paymentservice.repository.TransactionRepository;
import com.example.paymentservice.service.handler.OrderHandler;
import com.example.paymentservice.service.handler.OrderHandlerRegistry;
import com.example.paymentservice.kafka.producer.outbox.OutboxPublisher;
import com.example.paymentservice.util.validator.EventValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final TransactionRepository transactionRepository;
    private final EventValidator eventValidator;
    private final OrderHandlerRegistry orderHandlerRegistry;
    private final OutboxPublisher outboxPublisher;

    @Transactional
    public void processIncomingEvent(OrderEventStatus orderStatus, String payload) {
        OrderEvent orderEvent = eventValidator.validate(payload, orderStatus);
        OrderHandler orderHandler = orderHandlerRegistry.getHandler(orderStatus);
        orderHandler.process(orderEvent, payload);
    }

    @Transactional
    public RemitPaymentResponse remitPayment(RemitPaymentRequest request) {
        if (request == null || request.getUniqueOrderNumber() == null || request.getUniqueOrderNumber().isBlank()) {
            throw new IllegalArgumentException("uniqueOrderNumber must not be blank");
        }
        Transaction transaction = getTransactionForUpdate(request.getUniqueOrderNumber());

        if (transaction.getStatus() == TransactionStatus.COMPLETED) {
            return buildResponse("Transaction already processed");
        }
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new IllegalStateException(
                    "Transaction cannot be remitted from status " + transaction.getStatus()
            );
        }

        PaymentProcessedEvent paymentProcessedEvent = PaymentProcessedEvent.builder()
                .orderId(transaction.getOrderId())
                .uniqueOrderNumber(transaction.getUniqueOrderNumber())
                .paymentStatus(PaymentStatus.SUCCEEDED)
                .build();

        outboxPublisher.save(paymentProcessedEvent);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transactionRepository.save(transaction);

        return buildResponse("Transaction successfully processed");
    }

    private Transaction getTransactionForUpdate(String uniqueOrderNumber) {
        return transactionRepository.findByUniqueOrderNumberForUpdate(uniqueOrderNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Transaction not found for uniqueOrderNumber=" + uniqueOrderNumber
                ));
    }

    private RemitPaymentResponse buildResponse(String message) {
        RemitPaymentResponse response = new RemitPaymentResponse();
        response.setMessage(message);
        return response;
    }
}
