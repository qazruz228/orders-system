package com.example.paymentservice.service.handler;

import com.example.paymentservice.entity.Transaction;
import com.example.paymentservice.entity.enums.TransactionStatus;
import com.example.paymentservice.events.OrderEvent;
import com.example.paymentservice.repository.TransactionRepository;
import com.example.paymentservice.util.converter.ConverterEventToTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CancelOrderHandler implements OrderHandler {

    private final TransactionRepository transactionRepository;
    private final ConverterEventToTransaction converterEventToTransaction;

    @Override
    public void process(OrderEvent orderEvent) {
        Transaction transaction = transactionRepository.findByUniqueOrderNumber(orderEvent.getUniqueOrderNumber())
                .orElseGet(() -> converterEventToTransaction.convert(orderEvent));
        transaction.setStatus(TransactionStatus.CANCELLED);
        transactionRepository.save(transaction);
    }
}
