package com.example.similarproducts.exception;

public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String service, String message) {
        super("Error calling " + service + ": " + message);
    }
} 