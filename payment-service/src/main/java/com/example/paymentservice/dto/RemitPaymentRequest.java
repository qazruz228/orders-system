package com.example.paymentservice.dto;

import lombok.Data;

@Data
public class RemitPaymentRequest {
    private String uniqueOrderNumber;
}
