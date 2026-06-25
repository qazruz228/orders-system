package com.example.paymentservice.util.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonConverter {

    private final ObjectMapper objectMapper;

    public String toJson(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("value must not be null");
        }

        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object of type {}", value.getClass().getName(), e);
            throw new IllegalStateException("Failed to serialize object to JSON", e);
        }
    }

    public <T> T fromJson(String json, Class<T> targetType) {
        if (json == null || json.isBlank()) {
            throw new IllegalArgumentException("json must not be null or blank");
        }
        if (targetType == null) {
            throw new IllegalArgumentException("targetType must not be null");
        }

        try {
            return objectMapper.readValue(json, targetType);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize JSON to type {}", targetType.getName(), e);
            throw new IllegalArgumentException("Failed to deserialize JSON", e);
        }
    }
}
