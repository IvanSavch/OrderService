package com.innowise.orderservice.exception;

import java.io.Serial;

public class ServiceUnavailableException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "User service is unavailable";
    @Serial
    private static final long serialVersionUID = -7210740378630986293L;

    public ServiceUnavailableException() {
        super(DEFAULT_MESSAGE);
    }

    public ServiceUnavailableException(String message) {
        super(message);
    }

    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceUnavailableException(Throwable cause) {
        super(cause);
    }
}
