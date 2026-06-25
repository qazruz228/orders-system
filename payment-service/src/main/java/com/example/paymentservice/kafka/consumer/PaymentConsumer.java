package com.example.paymentservice.kafka.consumer;

import com.example.paymentservice.events.enums.OrderEventStatus;
import com.example.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private static final String ORDER_STATUS_HEADER = "orderStatus";


    private final PaymentService paymentService;

    @KafkaListener(
            topics = "${app.kafka.topics.order-events.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeCommand(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        log.info("Order event received topic={} partition={} offset={} key={} payloadBytes={}",
                record.topic(),
                record.partition(),
                record.offset(),
                record.key(),
                record.value() == null ? 0 : record.value().length());

        Header header = record.headers().lastHeader(ORDER_STATUS_HEADER);
        if (header == null) {
            throw new IllegalArgumentException("Missing Kafka header " + ORDER_STATUS_HEADER);
        }

        String statusValue = new String(header.value(), StandardCharsets.UTF_8);
        OrderEventStatus orderStatus = OrderEventStatus.fromString(statusValue);
        log.info(
                "Order event processing started topic={} partition={} offset={} key={} orderStatus={}",
                record.topic(),
                record.partition(),
                record.offset(),
                record.key(),
                orderStatus
        );
        paymentService.processIncomingEvent(orderStatus, record.value());
        acknowledgment.acknowledge();
        log.info(
                "Order event acknowledged topic={} partition={} offset={} key={} orderStatus={}",
                record.topic(),
                record.partition(),
                record.offset(),
                record.key(),
                orderStatus
        );
    }
}
