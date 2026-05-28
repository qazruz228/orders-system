package com.example.paymentservice.controller;

import com.example.paymentservice.dto.PaymentRequest;
import com.example.paymentservice.dto.PaymentResponse;
import com.example.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
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
    public ResponseEntity<PaymentResponse> remitPayment(@RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.remitPayment(request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/cancel")
    public ResponseEntity<PaymentResponse> cancelPayment(@RequestBody PaymentRequest request){
        PaymentResponse response = paymentService.cancelPayment(request);
        return ResponseEntity.ok(response);

    }


}
