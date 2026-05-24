package com.example.paymentservice.util.converter;

import com.example.paymentservice.entity.Transaction;
import com.example.paymentservice.entity.enums.TransactionStatus;
import com.example.paymentservice.events.OrderEvent;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ConverterEventToTransaction implements Converter<OrderEvent, Transaction> {

    @Override
    public Transaction convert(OrderEvent orderEvent) {
        Transaction transaction = new Transaction();
        transaction.setOrderId(orderEvent.getOrderId());
        transaction.setDeliveryAddress(orderEvent.getDeliveryAddress());
        transaction.setTotalAmount(orderEvent.getTotalAmount());
        transaction.setUniqueOrderNumber(orderEvent.getUniqueOrderNumber());
        transaction.setStatus(TransactionStatus.PENDING);
        return transaction;
    }
}
