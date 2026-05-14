package com.example.paymentservice.service;

import com.example.paymentservice.dto.RemitPaymentRequest;
import com.example.paymentservice.dto.RemitPaymentResponse;
import com.example.paymentservice.repository.ProcessedEventsRepository;
import com.example.paymentservice.service.handler.OrderHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ProcessedEventsRepository processedEventsRepository;

    private final OrderHandler orderHandler;



    public RemitPaymentResponse remitPayment(RemitPaymentRequest request){

        if (processedEventsRepository.existsByUniqueOrderNumber(request.getUniqueOrderNumber())){
            return;
        }

        orderHandler.processCommand();






    }




}
