package com.example.paymentservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableConfigurationProperties(KafkaTopicProperties.class)
public class KafkaTopicConfig {

    private final KafkaTopicProperties topicProperties;

    public KafkaTopicConfig(KafkaTopicProperties topicProperties) {
        this.topicProperties = topicProperties;
    }

    @Bean
    public NewTopic paymentEventTopic() {
        return TopicBuilder.name(topicProperties.getName())
                .partitions(topicProperties.getPartitions())
                .replicas(topicProperties.getReplicas())
                .configs(topicProperties.getConfigs())
                .build();
    }
}
