package com.example.paymentservice.controller;

import com.example.paymentservice.dto.RemitPaymentRequest;
import com.example.paymentservice.dto.RemitPaymentResponse;
import com.example.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/paymentApi")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/remit")
    public ResponseEntity<RemitPaymentResponse> remitPayment(@RequestBody RemitPaymentRequest request) {
        RemitPaymentResponse response = paymentService.remitPayment(request);
        return ResponseEntity.ok(response);
    }
}
