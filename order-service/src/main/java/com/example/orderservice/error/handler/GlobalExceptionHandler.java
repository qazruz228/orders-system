package com.example.orderservice.error.handler;

import com.example.orderservice.error.exception.JsonConversionException;
import com.example.orderservice.error.exception.OrderValidationException;
import com.example.orderservice.error.response.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JsonConversionException.class)
    public ResponseEntity<ApiErrorResponse> handleJsonConversionException(JsonConversionException exception) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "JSON conversion failed",
                List.of(exception.getMessage())
        );
    }

    @ExceptionHandler(OrderValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleOrderValidationException(OrderValidationException exception) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                exception.getErrors()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception
    ) {
        List<String> details = exception.getBindingResult().getAllErrors().stream()
                .map(error -> error instanceof FieldError fieldError
                        ? fieldError.getField() + ": " + fieldError.getDefaultMessage()
                        : error.getDefaultMessage())
                .toList();

        return buildResponse(HttpStatus.BAD_REQUEST, "Request validation failed", details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Malformed request body",
                List.of(exception.getMostSpecificCause().getMessage())
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(Exception exception) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected server error",
                List.of(exception.getMessage())
        );
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message, List<String> details) {
        ApiErrorResponse response = new ApiErrorResponse(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                details
        );
        return ResponseEntity.status(status).body(response);
    }
}
