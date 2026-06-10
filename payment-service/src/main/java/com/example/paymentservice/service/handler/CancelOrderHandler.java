//package com.example.paymentservice.service.handler;
//
//import com.example.paymentservice.entity.Transaction;
//import com.example.paymentservice.entity.enums.TransactionStatus;
//import com.example.paymentservice.events.OrderEvent;
//import com.example.paymentservice.events.ProcessedEvent;
//import com.example.paymentservice.events.enums.OrderEventStatus;
//import com.example.paymentservice.repository.ProcessedEventsRepository;
//import com.example.paymentservice.repository.TransactionRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class CancelOrderHandler implements OrderHandler {
//
//    private final ProcessedEventsRepository processedEventsRepository;
//    private final TransactionRepository transactionRepository;
//
//    @Override
//    public boolean supports(OrderEventStatus orderStatus) {
//        return orderStatus == OrderEventStatus.CANCELLED;
//    }
//
//    @Override
//    public void process(OrderEvent orderEvent, String payload) {
//        if (processedEventsRepository.existsByUniqueOrderNumberAndOrderStatus(orderEvent.getUniqueOrderNumber(), orderEvent.getStatus())) {
//            return;
//        }
//
//        ProcessedEvent processedEvent = new ProcessedEvent();
//        processedEvent.setUniqueOrderNumber(orderEvent.getUniqueOrderNumber());
//        processedEvent.setOrderStatus(orderEvent.getStatus());
//        processedEvent.setPayload(payload);
//        processedEventsRepository.save(processedEvent);
//
//        Transaction transaction = transactionRepository.findByUniqueOrderNumberForUpdate(orderEvent.getUniqueOrderNumber())
//                .orElse(null);
//        if (transaction == null) {
//            return;
//        }
//        if (transaction.getStatus() == TransactionStatus.COMPLETED || transaction.getStatus() == TransactionStatus.CANCELLED) {
//            return;
//        }
//
//        transaction.setStatus(TransactionStatus.CANCELLED);
//        transactionRepository.save(transaction);
//    }
//}
