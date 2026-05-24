package com.example.paymentservice.kafka.consumer;

import com.example.paymentservice.events.enums.OrderEventStatus;
import com.example.paymentservice.service.PaymentService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.Acknowledgment;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PaymentConsumerTest {

    @Test
    void shouldProcessRecordAndAcknowledgeOffset() {
        PaymentService paymentService = mock(PaymentService.class);
        PaymentConsumer paymentConsumer = new PaymentConsumer(paymentService);
        Acknowledgment acknowledgment = mock(Acknowledgment.class);
        String payload = "{\"uniqueOrderNumber\":\"order-123\",\"orderId\":1,\"totalAmount\":10}";

        ConsumerRecord<String, String> record = new ConsumerRecord<>("order-events", 0, 0L, "order-123", payload);
        record.headers().add(new RecordHeader("orderStatus", OrderEventStatus.CREATED.name().getBytes()));

        paymentConsumer.consumeCommand(record, acknowledgment);

        verify(paymentService).processIncomingEvent(OrderEventStatus.CREATED, payload);
        verify(acknowledgment).acknowledge();
    }
}
