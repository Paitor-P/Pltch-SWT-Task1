package com.viktor.task1.exception;

public class AlgorithmException extends RuntimeException {

    public AlgorithmException(String message) {
        super(message);
    }

    public AlgorithmException(String message, Throwable cause) {
        super(message, cause);
    }
}

