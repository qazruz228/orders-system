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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private EventValidator eventValidator;

    @Mock
    private OrderHandlerRegistry orderHandlerRegistry;

    @Mock
    private OrderHandler orderHandler;

    @Mock
    private OutboxPublisher outboxPublisher;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void shouldRouteIncomingEventByHeaderStatus() {
        String payload = "{\"orderId\":10,\"uniqueOrderNumber\":\"order-10\",\"totalAmount\":99.99}";
        OrderEvent orderEvent = OrderEvent.builder()
                .orderId(10L)
                .uniqueOrderNumber("order-10")
                .totalAmount(BigDecimal.valueOf(99.99))
                .status(OrderEventStatus.CREATED)
                .build();

        when(eventValidator.validate(payload, OrderEventStatus.CREATED)).thenReturn(orderEvent);
        when(orderHandlerRegistry.getHandler(OrderEventStatus.CREATED)).thenReturn(orderHandler);

        paymentService.processIncomingEvent(OrderEventStatus.CREATED, payload);

        verify(orderHandler).process(orderEvent, payload);
    }

    @Test
    void shouldRemitPendingTransactionAndPublishSucceededEvent() {
        Transaction transaction = new Transaction();
        transaction.setOrderId(25L);
        transaction.setUniqueOrderNumber("order-25");
        transaction.setStatus(TransactionStatus.PENDING);

        RemitPaymentRequest request = new RemitPaymentRequest();
        request.setUniqueOrderNumber("order-25");

        when(transactionRepository.findByUniqueOrderNumberForUpdate("order-25")).thenReturn(Optional.of(transaction));

        RemitPaymentResponse response = paymentService.remitPayment(request);

        ArgumentCaptor<PaymentProcessedEvent> eventCaptor = ArgumentCaptor.forClass(PaymentProcessedEvent.class);
        verify(outboxPublisher).save(eventCaptor.capture());
        verify(transactionRepository).save(transaction);

        PaymentProcessedEvent paymentEvent = eventCaptor.getValue();
        assertEquals(25L, paymentEvent.getOrderId());
        assertEquals("order-25", paymentEvent.getUniqueOrderNumber());
        assertEquals(PaymentStatus.SUCCEEDED, paymentEvent.getPaymentStatus());
        assertEquals(TransactionStatus.COMPLETED, transaction.getStatus());
        assertEquals("Transaction successfully processed", response.getMessage());
    }

    @Test
    void shouldReturnIdempotentResponseForCompletedTransaction() {
        Transaction transaction = new Transaction();
        transaction.setUniqueOrderNumber("order-50");
        transaction.setStatus(TransactionStatus.COMPLETED);

        RemitPaymentRequest request = new RemitPaymentRequest();
        request.setUniqueOrderNumber("order-50");

        when(transactionRepository.findByUniqueOrderNumberForUpdate("order-50")).thenReturn(Optional.of(transaction));

        RemitPaymentResponse response = paymentService.remitPayment(request);

        verify(outboxPublisher, never()).save(org.mockito.ArgumentMatchers.any());
        verify(transactionRepository, never()).save(org.mockito.ArgumentMatchers.any());
        assertEquals("Transaction already processed", response.getMessage());
    }

    @Test
    void shouldRejectRemitForNonPendingTransaction() {
        Transaction transaction = new Transaction();
        transaction.setUniqueOrderNumber("order-70");
        transaction.setStatus(TransactionStatus.CANCELLED);

        RemitPaymentRequest request = new RemitPaymentRequest();
        request.setUniqueOrderNumber("order-70");

        when(transactionRepository.findByUniqueOrderNumberForUpdate("order-70")).thenReturn(Optional.of(transaction));

        assertThrows(IllegalStateException.class, () -> paymentService.remitPayment(request));
        verify(outboxPublisher, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
