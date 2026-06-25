package com.example.orderservice.entity.enums.converter;

import com.example.orderservice.entity.enums.OrderEventStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class OrderStatusConverter implements AttributeConverter<OrderEventStatus, String> {


    @Override
    public String convertToDatabaseColumn(OrderEventStatus orderStatusEnum) {
        return (orderStatusEnum == null) ? null : orderStatusEnum.toString() ;
    }

    @Override
    public OrderEventStatus convertToEntityAttribute(String orderStatusEnum) {
        return (orderStatusEnum == null) ? null : OrderEventStatus.fromString(orderStatusEnum);
    }
}
