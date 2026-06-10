package com.example.orderservice.service.handler;


import com.example.orderservice.events.PaymentProcessedEvent;
import com.example.orderservice.events.enums.PaymentStatus;

public interface PaymentHandler {

    boolean supports(PaymentStatus paymentStatus);

    void process(PaymentProcessedEvent paymentEvent);


}
