package com.viktor.task1.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidInputExceptionTest {

    @Test
    void shouldCreateWithMessage() {
        InvalidInputException ex = new InvalidInputException("test message");
        assertEquals("test message", ex.getMessage());
    }

    @Test
    void shouldCreateWithMessageAndCause() {
        RuntimeException cause = new RuntimeException("cause");
        InvalidInputException ex = new InvalidInputException("test", cause);
        assertAll(
                () -> assertEquals("test", ex.getMessage()),
                () -> assertSame(cause, ex.getCause())
        );
    }

    @Test
    void shouldBeRuntimeException() {
        InvalidInputException ex = new InvalidInputException("msg");
        assertTrue(ex instanceof RuntimeException);
    }
}

