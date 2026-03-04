package com.innowise.orderservice.exception;

import java.io.Serial;

public class OrderNotFoundException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Order not found";
    @Serial
    private static final long serialVersionUID = 4958273235534362819L;

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
