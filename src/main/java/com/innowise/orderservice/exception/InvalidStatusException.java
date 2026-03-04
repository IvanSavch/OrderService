package com.innowise.orderservice.exception;

import java.io.Serial;

public class InvalidStatusException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Status must ber CREATED,IN_PROGRESS,DELIVERED,CANCELLED,PAID";
    @Serial
    private static final long serialVersionUID = 7153918953068529715L;

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
