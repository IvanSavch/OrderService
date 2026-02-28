package com.innowise.orderservice.exception;

public class ItemNotFoundException extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "Item not found";
    public ItemNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public ItemNotFoundException(String message) {
        super(message);
    }

    public ItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemNotFoundException(Throwable cause) {
        super(cause);
    }
}
