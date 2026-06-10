package com.example.orderservice.kafka.consumer;

import com.example.orderservice.config.PaymentHandlerConfig;
import com.example.orderservice.events.PaymentProcessedEvent;
import com.example.orderservice.service.handler.PaymentHandler;
import com.example.orderservice.util.converter.JsonConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
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
        log.info("Payment event received topic={} partition={} offset={} key={} payloadBytes={}",
                record.topic(),
                record.partition(),
                record.offset(),
                record.key(),
                record.value() == null ? 0 : record.value().length());
        PaymentProcessedEvent paymentEvent = jsonConverter.fromJson(record.value(), PaymentProcessedEvent.class);
        log.info(
                "Payment event processing started topic={} partition={} offset={} key={} orderId={} uniqueOrderNumber={} paymentStatus={}",
                record.topic(),
                record.partition(),
                record.offset(),
                record.key(),
                paymentEvent.getOrderId(),
                paymentEvent.getUniqueOrderNumber(),
                paymentEvent.getPaymentStatus()
        );
        PaymentHandler paymentHandler = paymentHandlerConfig.getHandler(paymentEvent.getPaymentStatus());
        paymentHandler.process(paymentEvent);
        acknowledgment.acknowledge();
        log.info(
                "Payment event acknowledged topic={} partition={} offset={} key={} orderId={} uniqueOrderNumber={} paymentStatus={}",
                record.topic(),
                record.partition(),
                record.offset(),
                record.key(),
                paymentEvent.getOrderId(),
                paymentEvent.getUniqueOrderNumber(),
                paymentEvent.getPaymentStatus()
        );
    }
}
