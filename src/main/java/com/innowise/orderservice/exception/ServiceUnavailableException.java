package com.innowise.orderservice.exception;

public class ServiceUnavailableException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "User service is unavailable";
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
