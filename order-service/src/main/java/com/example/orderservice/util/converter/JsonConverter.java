package com.example.orderservice.util.converter;

import com.example.orderservice.error.exception.JsonConversionException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonConverter {

    private final ObjectMapper objectMapper ;

    public String toJson(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("value must not be null");
        }

        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.error("Serialization error. Type={}", value.getClass().getSimpleName(), e);
            throw new JsonConversionException("Failed to serialize object", e);
        }
    }

    public <T> T fromJson(String json, Class<T> targetType) {
        if (json == null || json.isBlank()) {
            throw new IllegalArgumentException("json must not be blank");
        }
        if (targetType == null) {
            throw new IllegalArgumentException("targetType must not be null");
        }

        try {
            return objectMapper.readValue(json, targetType);
        } catch (JsonProcessingException e) {
            log.error("Deserialization error. TargetType={}", targetType.getSimpleName(), e);
            throw new JsonConversionException("Failed to deserialize JSON", e);
        }
    }
}
