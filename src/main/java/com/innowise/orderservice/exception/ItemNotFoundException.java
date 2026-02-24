package com.innowise.orderservice.exception;

public class ItemNotFoundException extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "Item not found";
    public ItemNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
}
