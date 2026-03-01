package com.viktor.task1.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ApplicationRunnerTest {

    @Test
    void shouldInstantiateApplicationRunner() {
        assertDoesNotThrow(() -> new ApplicationRunner());
    }
}

