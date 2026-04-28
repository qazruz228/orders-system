package com.example.orderservice.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Validated
@ConfigurationProperties(prefix = "app.kafka.topics.order-events")
public class KafkaTopicProperties {

    @NotBlank
    private String name;

    @Min(1)
    private int partitions;

    @Min(1)
    private short replicas;

    @NotEmpty
    private Map<String, String> configs = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPartitions() {
        return partitions;
    }

    public void setPartitions(int partitions) {
        this.partitions = partitions;
    }

    public short getReplicas() {
        return replicas;
    }

    public void setReplicas(short replicas) {
        this.replicas = replicas;
    }

    public Map<String, String> getConfigs() {
        return configs;
    }

    public void setConfigs(Map<String, String> configs) {
        this.configs = configs;
    }
}
