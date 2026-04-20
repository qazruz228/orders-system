package com.example.orderservice.entity.enums.converter;

import com.example.orderservice.entity.enums.OrderStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class OrderStatusConverter implements AttributeConverter<OrderStatus, String> {


    @Override
    public String convertToDatabaseColumn(OrderStatus orderStatusEnum) {
        return (orderStatusEnum == null) ? null : orderStatusEnum.toString() ;
    }

    @Override
    public OrderStatus convertToEntityAttribute(String orderStatusEnum) {
        return (orderStatusEnum == null) ? null : OrderStatus.valueOf(orderStatusEnum);
    }
}
