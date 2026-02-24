package com.innowise.orderservice.exception;

public class InvalidStatusException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Status must ber CREATED,IN_PROGRESS,DELIVERED,CANCELLED,PAID";
    public InvalidStatusException() {
        super(DEFAULT_MESSAGE);
    }
}
