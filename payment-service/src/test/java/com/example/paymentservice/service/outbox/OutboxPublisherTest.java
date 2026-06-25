package com.example.paymentservice.service.outbox;

import com.example.paymentservice.events.PaymentProcessedEvent;
import com.example.paymentservice.events.enums.PaymentStatus;
import com.example.paymentservice.events.outbox.OutboxEvent;
import com.example.paymentservice.events.outbox.OutboxStatus;
import com.example.paymentservice.kafka.producer.outbox.OutboxPublisher;
import com.example.paymentservice.repository.OutboxEventRepository;
import com.example.paymentservice.util.converter.JsonConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxPublisherTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private JsonConverter jsonConverter;

    @InjectMocks
    private OutboxPublisher outboxPublisher;

    @Test
    void shouldSaveNewOutboxEventForPaymentConfirmation() {
        PaymentProcessedEvent paymentProcessedEvent = PaymentProcessedEvent.builder()
                .orderId(15L)
                .uniqueOrderNumber("order-15")
                .paymentStatus(PaymentStatus.SUCCEEDED)
                .build();

        when(jsonConverter.toJson(paymentProcessedEvent)).thenReturn("{\"paymentStatus\":\"SUCCEEDED\"}");

        outboxPublisher.save(paymentProcessedEvent);

        ArgumentCaptor<OutboxEvent> eventCaptor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository).save(eventCaptor.capture());

        OutboxEvent outboxEvent = eventCaptor.getValue();
        assertEquals("{\"paymentStatus\":\"SUCCEEDED\"}", outboxEvent.getPayload());
        assertEquals(15L, outboxEvent.getOrderId());
        assertEquals("order-15", outboxEvent.getUniqueOrderNumber());
        assertEquals(PaymentStatus.SUCCEEDED, outboxEvent.getPaymentStatus());
        assertEquals(OutboxStatus.NEW, outboxEvent.getOutboxStatus());
        assertEquals(0, outboxEvent.getRetryCount());
    }
}
