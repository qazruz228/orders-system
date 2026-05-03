package com.example.orderservice.error.exception;

import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
public class OrderValidationException extends RuntimeException {
    private final List<String> errors;

    public OrderValidationException(List<String> errors) {
        super("Order validation failed");
        this.errors = List.copyOf(Objects.requireNonNull(errors, "errors must not be null"));
    }
}
