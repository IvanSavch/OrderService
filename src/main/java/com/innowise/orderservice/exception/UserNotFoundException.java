package com.innowise.orderservice.exception;

import java.io.Serial;

public class UserNotFoundException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "User not found";
    @Serial
    private static final long serialVersionUID = 5894115092315704847L;

    public UserNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotFoundException(Throwable cause) {
        super(cause);
    }
}
