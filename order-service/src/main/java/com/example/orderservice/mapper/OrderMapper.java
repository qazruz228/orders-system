package com.example.orderservice.mapper;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true),
        uses = OrderItemMapper.class)
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(source = "orderItems", target = "items")
    Order toOrder(CreateOrderRequest request);

    @AfterMapping
    default void linkOrderItems(@MappingTarget Order order) {
        if (order.getItems() == null) {
            return;
        }

        for (OrderItem item : order.getItems()) {
            item.setOrder(order);
        }
    }
}
