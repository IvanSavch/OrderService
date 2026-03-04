package com.innowise.orderservice.exception;

import java.io.Serial;

public class ItemNotFoundException extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "Item not found";
    @Serial
    private static final long serialVersionUID = 3357449092888511426L;

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
