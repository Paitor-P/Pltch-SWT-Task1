package com.viktor.task1.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnsupportedShapeExceptionTest {

    @Test
    void shouldCreateWithMessage() {
        UnsupportedShapeException ex = new UnsupportedShapeException("unsupported");
        assertEquals("unsupported", ex.getMessage());
    }

    @Test
    void shouldCreateWithMessageAndCause() {
        Exception cause = new Exception("root");
        UnsupportedShapeException ex = new UnsupportedShapeException("msg", cause);
        assertAll(
                () -> assertEquals("msg", ex.getMessage()),
                () -> assertSame(cause, ex.getCause())
        );
    }

    @Test
    void shouldBeRuntimeException() {
        UnsupportedShapeException ex = new UnsupportedShapeException("x");
        assertFalse(ex.getMessage().isEmpty());
        assertTrue(ex instanceof RuntimeException);
    }
}


