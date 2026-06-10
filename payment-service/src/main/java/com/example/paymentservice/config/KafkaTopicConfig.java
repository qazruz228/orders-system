package com.example.paymentservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Map;

@Configuration
@ConditionalOnProperty(value = "app.kafka.admin.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(KafkaTopicProperties.class)
public class KafkaTopicConfig {

    private final KafkaTopicProperties paymentTopicProperties;
    @Value("${app.kafka.topics.order-events.name}")
    private String orderTopicName;
    @Value("${app.kafka.topics.order-events.partitions}")
    private int orderTopicPartitions;
    @Value("${app.kafka.topics.order-events.replicas}")
    private int orderTopicReplicas;

    public KafkaTopicConfig(KafkaTopicProperties paymentTopicProperties) {
        this.paymentTopicProperties = paymentTopicProperties;
    }

    @Bean
    public NewTopic paymentEventTopic() {
        return TopicBuilder.name(paymentTopicProperties.getName())
                .partitions(paymentTopicProperties.getPartitions())
                .replicas(paymentTopicProperties.getReplicas())
                .configs(paymentTopicProperties.getConfigs())
                .build();
    }

    @Bean
    public NewTopic orderEventDlqTopic() {
        return TopicBuilder.name(orderTopicName + ".dlq")
                .partitions(orderTopicPartitions)
                .replicas(orderTopicReplicas)
                .configs(paymentTopicProperties.getConfigs())
                .build();
    }
}
