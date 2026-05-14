package com.example.paymentservice.service.handler;

import com.example.paymentservice.entity.Transaction;
import com.example.paymentservice.events.OrderEvent;
import com.example.paymentservice.events.enums.OrderEventStatus;
import com.example.paymentservice.repository.TransactionRepository;
import com.example.paymentservice.util.converter.ConverterEventToTransaction;
import com.example.paymentservice.util.validator.EventValidator;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateOrderHandler implements OrderHandler {


    private final EventValidator eventValidator;
    private final ConverterEventToTransaction converterEventToTransaction;
    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<?, ?> kafkaTemplate;

    @Override
    public void processCommand(ConsumerRecord<String, String> record){

        String value = record.value();
        String status = record.headers().lastHeader("OrderStatus");
        OrderEvent orderEvent = eventValidator.validate(value);


        if (status == OrderEventStatus.CREATED){
            ProducerRecord<String, String> record;
            kafkaTemplate.send(orderEvent.getOrderId(), )

        }

    }


    public void createTransaction(OrderEvent orderEvent){

     Transaction transaction = converterEventToTransaction.convert(orderEvent);
     transactionRepository.save(transaction);






    }










}
