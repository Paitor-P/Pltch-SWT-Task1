package com.viktor.task1.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlgorithmExceptionTest {

    @Test
    void shouldCreateWithMessage() {
        AlgorithmException ex = new AlgorithmException("algo error");
        assertEquals("algo error", ex.getMessage());
    }

    @Test
    void shouldCreateWithMessageAndCause() {
        Throwable cause = new IllegalStateException("state");
        AlgorithmException ex = new AlgorithmException("msg", cause);
        assertAll(
                () -> assertEquals("msg", ex.getMessage()),
                () -> assertSame(cause, ex.getCause())
        );
    }

    @Test
    void shouldBeRuntimeException() {
        assertTrue(new AlgorithmException("x") instanceof RuntimeException);
    }
}

