package com.example.orderservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Map;

@Configuration
@EnableConfigurationProperties(KafkaTopicProperties.class)
public class KafkaTopicConfig {

    private final KafkaTopicProperties orderTopicProperties;
    @Value("${app.kafka.topics.payment-events.name}")
    private String paymentTopicName;
    @Value("${app.kafka.topics.payment-events.partitions}")
    private int paymentTopicPartitions;
    @Value("${app.kafka.topics.payment-events.replicas}")
    private int paymentTopicReplicas;
    @Value("${app.kafka.topics.payment-events.configs.min.insync.replicas}")
    private String paymentTopicMinInsyncReplicas;
    @Value("${app.kafka.topics.payment-events.configs.cleanup.policy}")
    private String paymentTopicCleanupPolicy;
    @Value("${app.kafka.topics.payment-events.configs.retention.ms}")
    private String paymentTopicRetentionMs;

    public KafkaTopicConfig(KafkaTopicProperties orderTopicProperties) {
        this.orderTopicProperties = orderTopicProperties;
    }

    @Bean
    public NewTopic orderEventTopic() {
        return TopicBuilder.name(orderTopicProperties.getName())
                .partitions(orderTopicProperties.getPartitions())
                .replicas(orderTopicProperties.getReplicas())
                .configs(orderTopicProperties.getConfigs())
                .build();
    }

    @Bean
    public NewTopic paymentEventTopic() {
        return TopicBuilder.name(paymentTopicName)
                .partitions(paymentTopicPartitions)
                .replicas(paymentTopicReplicas)
                .configs(paymentTopicConfigs())
                .build();
    }

    @Bean
    public NewTopic paymentEventDlqTopic() {
        return TopicBuilder.name(paymentTopicName + ".dlq")
                .partitions(paymentTopicPartitions)
                .replicas(paymentTopicReplicas)
                .configs(paymentTopicConfigs())
                .build();
    }

    private Map<String, String> paymentTopicConfigs() {
        return Map.of(
                "min.insync.replicas", paymentTopicMinInsyncReplicas,
                "cleanup.policy", paymentTopicCleanupPolicy,
                "retention.ms", paymentTopicRetentionMs
        );
    }
}
