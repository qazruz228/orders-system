package com.example.paymentservice.util.validator;

import com.example.paymentservice.entity.ProcessedEvent;
import com.example.paymentservice.events.OrderEvent;
import com.example.paymentservice.repository.ProcessedEventsRepository;
import com.example.paymentservice.util.converter.JsonConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventValidator {


    private final ProcessedEventsRepository processedEventsRepository;
    private final JsonConverter jsonConverter;


    public OrderEvent validate(String value){

        OrderEvent orderEvent = jsonConverter.fromJson(value, OrderEvent.class);

        if (processedEventsRepository.existsByUniqueOrderNumber(orderEvent.getUniqueOrderNumber())){
            return;
        }

        ProcessedEvent processedEvent = new ProcessedEvent();
        processedEvent.setUniqueOrderNumber(orderEvent.getUniqueOrderNumber());
        processedEventsRepository.save(processedEvent);

        return orderEvent;


    }


}
