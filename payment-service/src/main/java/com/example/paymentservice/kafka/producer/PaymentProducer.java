package com.example.paymentservice.kafka.producer;

import com.example.paymentservice.config.KafkaTopicProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTopicProperties kafkaTopicProperties;

    public void send(String key, String payload) {
        kafkaTemplate.send(new ProducerRecord<>(kafkaTopicProperties.getName(), key, payload));
    }
}
