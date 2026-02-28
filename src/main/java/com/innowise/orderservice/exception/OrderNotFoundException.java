package com.innowise.orderservice.exception;

public class OrderNotFoundException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Order not found";
    public OrderNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderNotFoundException(Throwable cause) {
        super(cause);
    }
}
