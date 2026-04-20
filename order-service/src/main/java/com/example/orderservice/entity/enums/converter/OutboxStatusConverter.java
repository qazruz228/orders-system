package com.example.orderservice.entity.enums.converter;

import com.example.orderservice.entity.enums.OutboxStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class OutboxStatusConverter implements AttributeConverter<OutboxStatus, String> {
    @Override
    public String convertToDatabaseColumn(OutboxStatus outboxStatusEnum) {
        return (outboxStatusEnum == null) ? null : outboxStatusEnum.toString();
    }

    @Override
    public OutboxStatus convertToEntityAttribute(String outboxStatusEnum) {
        return (outboxStatusEnum == null) ? null : OutboxStatus.valueOf(outboxStatusEnum);
    }
}
