package com.example.orderservice.config;

import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    private final KafkaProperties kafkaProperties;

    public KafkaProducerConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {

        Map<String, Object> producerProperties = new HashMap<>(kafkaProperties.buildProducerProperties());

        return new DefaultKafkaProducerFactory<>(producerProperties);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory);
//        эта строка используется если в коде есть метрики. Это нужно для того, чтобы отправка сообщения в Kafka была видна в monitoring stack: сколько было отправок, сколько ошибок, какие latency, как publish связан с остальной трассировкой запроса
//        kafkaTemplate.setObservationEnabled(true);
        return kafkaTemplate;
    }
}
