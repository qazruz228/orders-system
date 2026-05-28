package com.example.orderservice.kafka.consumer;

import com.example.orderservice.config.PaymentHandlerConfig;
import com.example.orderservice.events.PaymentProcessedEvent;
import com.example.orderservice.service.handler.PaymentHandler;
import com.example.orderservice.util.converter.JsonConverter;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final JsonConverter jsonConverter;
    private final PaymentHandlerConfig paymentHandlerConfig;
    @KafkaListener(
            topics = "${app.kafka.topics.payment-events.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePaymentEvent(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        PaymentProcessedEvent paymentEvent = jsonConverter.fromJson(record.value(), PaymentProcessedEvent.class);
        PaymentHandler paymentHandler = paymentHandlerConfig.getHandler(paymentEvent.getPaymentStatus());
        paymentHandler.process(paymentEvent);
        acknowledgment.acknowledge();
    }
}
