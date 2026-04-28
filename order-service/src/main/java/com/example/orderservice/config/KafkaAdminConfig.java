package com.example.orderservice.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaAdminConfig {

    private final KafkaProperties kafkaProperties;
    private final String applicationName;

    public KafkaAdminConfig(KafkaProperties kafkaProperties,
                            @Value("${spring.application.name}") String applicationName) {
        this.kafkaProperties = kafkaProperties;
        this.applicationName = applicationName;
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> adminProperties = new HashMap<>(kafkaProperties.buildAdminProperties());
        adminProperties.put(AdminClientConfig.CLIENT_ID_CONFIG, applicationName + "-admin");
        adminProperties.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 10_000);

        KafkaAdmin kafkaAdmin = new KafkaAdmin(adminProperties);
        kafkaAdmin.setFatalIfBrokerNotAvailable(true);
        return kafkaAdmin;
    }
}
