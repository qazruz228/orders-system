package com.example.paymentservice.service;

import com.example.paymentservice.dto.PaymentRequest;
import com.example.paymentservice.dto.PaymentResponse;
import com.example.paymentservice.entity.Transaction;
import com.example.paymentservice.entity.enums.TransactionStatus;
import com.example.paymentservice.events.OrderEvent;
import com.example.paymentservice.events.PaymentProcessedEvent;
import com.example.paymentservice.events.enums.OrderEventStatus;
import com.example.paymentservice.events.enums.PaymentStatus;
import com.example.paymentservice.repository.TransactionRepository;
import com.example.paymentservice.service.handler.OrderHandler;
import com.example.paymentservice.config.OrderHandlerConfig;
import com.example.paymentservice.kafka.producer.outbox.OutboxPublisher;
import com.example.paymentservice.util.validator.EventValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final TransactionRepository transactionRepository;
    private final EventValidator eventValidator;
    private final OrderHandlerConfig orderHandlerConfig;
    private final OutboxPublisher outboxPublisher;

    @Transactional
    public void processIncomingEvent(OrderEventStatus orderStatus, String payload) {
        OrderEvent orderEvent = eventValidator.validate(payload, orderStatus);
        log.info(
                "Payment service handling order event orderId={} uniqueOrderNumber={} orderStatus={}",
                orderEvent.getOrderId(),
                orderEvent.getUniqueOrderNumber(),
                orderStatus
        );
        OrderHandler orderHandler = orderHandlerConfig.getHandler(orderStatus);
        orderHandler.process(orderEvent, payload);
    }

    @Transactional
    public PaymentResponse remitPayment(PaymentRequest request) {
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
        log.info(
                "Payment remitted orderId={} uniqueOrderNumber={} paymentStatus={} transactionStatus={}",
                transaction.getOrderId(),
                transaction.getUniqueOrderNumber(),
                PaymentStatus.SUCCEEDED,
                transaction.getStatus()
        );

        return buildResponse("Transaction successfully processed");
    }

    @Transactional
    public PaymentResponse cancelPayment(PaymentRequest request){

        if (request == null || request.getUniqueOrderNumber() == null || request.getUniqueOrderNumber().isBlank()) {
            throw new IllegalArgumentException("uniqueOrderNumber must not be blank");
        }
        Transaction transaction = getTransactionForUpdate(request.getUniqueOrderNumber());

        if (transaction.getStatus() == TransactionStatus.CANCELLED) {
            return buildResponse("Transaction already cancelled");
        }
        if (transaction.getStatus() != TransactionStatus.COMPLETED) {
            throw new IllegalStateException("Transaction already processed ");
        }
        PaymentProcessedEvent paymentProcessedEvent = PaymentProcessedEvent.builder()
                .orderId(transaction.getOrderId())
                .uniqueOrderNumber(transaction.getUniqueOrderNumber())
                .paymentStatus(PaymentStatus.CANCELLED)
                .build();

        outboxPublisher.save(paymentProcessedEvent);
        transaction.setStatus(TransactionStatus.CANCELLED);
        transactionRepository.save(transaction);
        log.info(
                "Payment cancelled orderId={} uniqueOrderNumber={} paymentStatus={} transactionStatus={}",
                transaction.getOrderId(),
                transaction.getUniqueOrderNumber(),
                PaymentStatus.CANCELLED,
                transaction.getStatus()
        );

        return buildResponse("Transaction successfully cancelled");
    }

    private Transaction getTransactionForUpdate(String uniqueOrderNumber) {
        return transactionRepository.findByUniqueOrderNumberForUpdate(uniqueOrderNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Transaction not found for uniqueOrderNumber=" + uniqueOrderNumber
                ));
    }

    private PaymentResponse buildResponse(String message) {
        PaymentResponse response = new PaymentResponse();
        response.setMessage(message);
        return response;
    }
}
