package com.viktor.task1.exception;

public class UnsupportedShapeException extends RuntimeException {

    public UnsupportedShapeException(String message) {
        super(message);
    }

    public UnsupportedShapeException(String message, Throwable cause) {
        super(message, cause);
    }
}

