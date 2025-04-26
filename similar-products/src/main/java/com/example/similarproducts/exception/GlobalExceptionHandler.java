package com.example.similarproducts.exception;

import com.example.similarproducts.model.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiError> handleProductNotFoundException(ProductNotFoundException ex, WebRequest request) {
        ApiError apiError = new ApiError(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            request.getDescription(false)
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ApiError> handleExternalServiceException(ExternalServiceException ex, WebRequest request) {
        ApiError apiError = new ApiError(
            HttpStatus.SERVICE_UNAVAILABLE.value(),
            "Service Unavailable",
            ex.getMessage(),
            request.getDescription(false)
        );
        return new ResponseEntity<>(apiError, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ApiError> handleWebClientResponseException(WebClientResponseException ex, WebRequest request) {
        ApiError apiError = new ApiError(
            ex.getStatusCode().value(),
            ex.getStatusCode().toString(),
            ex.getMessage(),
            request.getDescription(false)
        );
        return new ResponseEntity<>(apiError, ex.getStatusCode());
    }
} 