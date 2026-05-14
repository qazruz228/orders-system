package com.example.paymentservice.kafka.consumer;

import com.example.paymentservice.events.enums.OrderEventStatus;
import com.example.paymentservice.service.handler.OrderHandler;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final Map<OrderEventStatus, OrderHandler> commandHandlers;

    @KafkaListener(topics = "payment-events", groupId = "sdos",
            containerFactory = "kafkaListenerContainerFactory")
    public void consumeCommand(ConsumerRecord<String, String> record){

       String header = String.valueOf(record.headers().lastHeader("orderStatus"));

       String s = record.key();
       String value = record.value();
       commandHandlers.get(header).processCommand(s , value);

    }




}
