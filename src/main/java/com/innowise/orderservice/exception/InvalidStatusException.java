package com.innowise.orderservice.exception;

public class InvalidStatusException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Status must ber CREATED,IN_PROGRESS,DELIVERED,CANCELLED,PAID";
    public InvalidStatusException() {
        super(DEFAULT_MESSAGE);
    }

    public InvalidStatusException(String message) {
        super(message);
    }

    public InvalidStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidStatusException(Throwable cause) {
        super(cause);
    }
}
