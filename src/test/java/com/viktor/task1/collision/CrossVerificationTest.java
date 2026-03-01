package com.viktor.task1.collision;

import com.viktor.task1.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.jupiter.api.Assertions.*;

class CrossVerificationTest {

    private AnalyticalCollisionDetector analytical;
    private DiscreteCollisionDetector discrete;

    @BeforeEach
    void setUp() {
        analytical = new AnalyticalCollisionDetector();
        discrete = new DiscreteCollisionDetector(10000);
    }

    @Test
    void shouldAgreeOnCircleCircleCollision() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 1, 0, 0, 20);
        MovingObject b = new MovingObject(2, new Circle(20, 0, 5), -1, 0, 0, 20);

        CollisionResult analyticalResult = analytical.detect(a, b);
        CollisionResult discreteResult = discrete.detect(a, b);

        assertEquals(analyticalResult.collision(), discreteResult.collision());
        if (analyticalResult.collision() && discreteResult.collision()) {
            assertThat(discreteResult.time(), closeTo(analyticalResult.time(), 0.5));
        }
    }

    @Test
    void shouldAgreeOnCircleCircleNoCollision() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 1), 0, 1, 0, 10);
        MovingObject b = new MovingObject(2, new Circle(100, 0, 1), 0, -1, 0, 10);

        CollisionResult analyticalResult = analytical.detect(a, b);
        CollisionResult discreteResult = discrete.detect(a, b);

        assertEquals(analyticalResult.collision(), discreteResult.collision());
    }

    @Test
    void shouldAgreeOnAABBCollision() {
        MovingObject a = new MovingObject(1, new AABB(0, 0, 4, 4), 1, 0, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(10, 0, 14, 4), -1, 0, 0, 20);

        CollisionResult analyticalResult = analytical.detect(a, b);
        CollisionResult discreteResult = discrete.detect(a, b);

        assertEquals(analyticalResult.collision(), discreteResult.collision());
        if (analyticalResult.collision() && discreteResult.collision()) {
            assertThat(discreteResult.time(), closeTo(analyticalResult.time(), 0.5));
        }
    }

    @Test
    void shouldAgreeOnAABBNoCollision() {
        MovingObject a = new MovingObject(1, new AABB(0, 0, 2, 2), 0, 1, 0, 10);
        MovingObject b = new MovingObject(2, new AABB(100, 100, 102, 102), 0, -1, 0, 10);

        CollisionResult analyticalResult = analytical.detect(a, b);
        CollisionResult discreteResult = discrete.detect(a, b);

        assertFalse(analyticalResult.collision());
        assertFalse(discreteResult.collision());
    }

    @Test
    void shouldAgreeOnOverlappingStart() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 0, 0, 0, 10);
        MovingObject b = new MovingObject(2, new Circle(3, 0, 5), 0, 0, 0, 10);

        CollisionResult analyticalResult = analytical.detect(a, b);
        CollisionResult discreteResult = discrete.detect(a, b);

        assertTrue(analyticalResult.collision());
        assertTrue(discreteResult.collision());
        assertThat(analyticalResult.time(), closeTo(0.0, 0.1));
        assertThat(discreteResult.time(), closeTo(0.0, 0.1));
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> crossVerificationScenarios() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(0, 0, 3, 2, 0, 20, 0, 3, -2, 0, true),
                org.junit.jupiter.params.provider.Arguments.of(0, 0, 1, 0, 0, 100, 0, 1, 0, 0, false),
                org.junit.jupiter.params.provider.Arguments.of(0, 0, 5, 0, 0, 3, 0, 5, 0, 0, true),
                org.junit.jupiter.params.provider.Arguments.of(0, 0, 2, 1, 1, 50, 50, 2, -1, -1, true)
        );
    }

    @ParameterizedTest
    @MethodSource("crossVerificationScenarios")
    void shouldAgreeOnMultipleScenarios(double ax, double ay, double ar, double avx, double avy,
                                         double bx, double by, double br, double bvx, double bvy,
                                         boolean expectedCollision) {
        MovingObject a = new MovingObject(1, new Circle(ax, ay, ar), avx, avy, 0, 30);
        MovingObject b = new MovingObject(2, new Circle(bx, by, br), bvx, bvy, 0, 30);

        CollisionResult analyticalResult = analytical.detect(a, b);
        CollisionResult discreteResult = discrete.detect(a, b);

        assertEquals(expectedCollision, analyticalResult.collision());
        assertEquals(analyticalResult.collision(), discreteResult.collision());

        if (analyticalResult.collision() && discreteResult.collision()) {
            assertThat(discreteResult.time(), closeTo(analyticalResult.time(), 1.0));
        }
    }
}

