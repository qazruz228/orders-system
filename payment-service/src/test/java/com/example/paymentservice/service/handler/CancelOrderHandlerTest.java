package com.example.paymentservice.service.handler;

import com.example.paymentservice.entity.Transaction;
import com.example.paymentservice.entity.enums.TransactionStatus;
import com.example.paymentservice.events.OrderEvent;
import com.example.paymentservice.events.enums.OrderEventStatus;
import com.example.paymentservice.repository.ProcessedEventsRepository;
import com.example.paymentservice.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CancelOrderHandlerTest {

    @Mock
    private ProcessedEventsRepository processedEventsRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private CancelOrderHandler cancelOrderHandler;

    @Test
    void shouldCancelPendingTransaction() {
        OrderEvent event = OrderEvent.builder()
                .uniqueOrderNumber("order-1")
                .status(OrderEventStatus.CANCELLED)
                .build();
        Transaction transaction = new Transaction();
        transaction.setStatus(TransactionStatus.PENDING);

        when(processedEventsRepository.existsByUniqueOrderNumberAndOrderStatus("order-1", OrderEventStatus.CANCELLED))
                .thenReturn(false);
        when(transactionRepository.findByUniqueOrderNumberForUpdate("order-1"))
                .thenReturn(Optional.of(transaction));

        cancelOrderHandler.process(event, "{}");

        verify(processedEventsRepository).save(ArgumentMatchers.any());
        verify(transactionRepository).save(transaction);
    }

    @Test
    void shouldSkipWhenAlreadyProcessed() {
        OrderEvent event = OrderEvent.builder()
                .uniqueOrderNumber("order-2")
                .status(OrderEventStatus.CANCELLED)
                .build();
        when(processedEventsRepository.existsByUniqueOrderNumberAndOrderStatus("order-2", OrderEventStatus.CANCELLED))
                .thenReturn(true);

        cancelOrderHandler.process(event, "{}");

        verify(processedEventsRepository, never()).save(ArgumentMatchers.any());
        verify(transactionRepository, never()).findByUniqueOrderNumberForUpdate(ArgumentMatchers.anyString());
    }
}
