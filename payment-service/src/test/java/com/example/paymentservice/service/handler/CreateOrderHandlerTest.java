package com.example.paymentservice.service.handler;

import com.example.paymentservice.entity.Transaction;
import com.example.paymentservice.entity.enums.TransactionStatus;
import com.example.paymentservice.events.OrderEvent;
import com.example.paymentservice.events.ProcessedEvent;
import com.example.paymentservice.events.enums.OrderEventStatus;
import com.example.paymentservice.repository.ProcessedEventsRepository;
import com.example.paymentservice.repository.TransactionRepository;
import com.example.paymentservice.util.converter.ConverterEventToTransaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateOrderHandlerTest {

    @Mock
    private ConverterEventToTransaction converterEventToTransaction;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ProcessedEventsRepository processedEventsRepository;

    @InjectMocks
    private CreateOrderHandler createOrderHandler;

    @Test
    void shouldPersistProcessedEventAndCreatePendingTransaction() {
        OrderEvent orderEvent = OrderEvent.builder()
                .orderId(11L)
                .uniqueOrderNumber("order-11")
                .deliveryAddress("Moscow")
                .totalAmount(BigDecimal.TEN)
                .status(OrderEventStatus.CREATED)
                .build();

        Transaction transaction = new Transaction();
        transaction.setUniqueOrderNumber("order-11");

        when(processedEventsRepository.existsByUniqueOrderNumberAndOrderStatus("order-11", OrderEventStatus.CREATED))
                .thenReturn(false);
        when(transactionRepository.findByUniqueOrderNumber("order-11")).thenReturn(Optional.empty());
        when(converterEventToTransaction.convert(orderEvent)).thenReturn(transaction);

        createOrderHandler.process(orderEvent, "{\"uniqueOrderNumber\":\"order-11\"}");

        ArgumentCaptor<ProcessedEvent> processedEventCaptor = ArgumentCaptor.forClass(ProcessedEvent.class);
        verify(processedEventsRepository).save(processedEventCaptor.capture());
        verify(transactionRepository).save(transaction);

        ProcessedEvent processedEvent = processedEventCaptor.getValue();
        assertEquals("order-11", processedEvent.getUniqueOrderNumber());
        assertEquals(OrderEventStatus.CREATED, processedEvent.getOrderStatus());
        assertEquals("{\"uniqueOrderNumber\":\"order-11\"}", processedEvent.getPayload());
        assertEquals(TransactionStatus.PENDING, transaction.getStatus());
    }

    @Test
    void shouldSkipDuplicateEvent() {
        OrderEvent orderEvent = OrderEvent.builder()
                .uniqueOrderNumber("order-12")
                .status(OrderEventStatus.CREATED)
                .build();

        when(processedEventsRepository.existsByUniqueOrderNumberAndOrderStatus("order-12", OrderEventStatus.CREATED))
                .thenReturn(true);

        createOrderHandler.process(orderEvent, "{}");

        verify(processedEventsRepository, never()).save(org.mockito.ArgumentMatchers.any());
        verify(transactionRepository, never()).findByUniqueOrderNumber(org.mockito.ArgumentMatchers.anyString());
        verify(transactionRepository, never()).save(org.mockito.ArgumentMatchers.any());
        assertTrue(createOrderHandler.supports(OrderEventStatus.CREATED));
        assertFalse(createOrderHandler.supports(OrderEventStatus.PENDING));
    }
}
