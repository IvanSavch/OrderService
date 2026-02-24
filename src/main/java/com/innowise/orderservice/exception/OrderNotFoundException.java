package com.innowise.orderservice.exception;

public class OrderNotFoundException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Order not found";
    public OrderNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
}
