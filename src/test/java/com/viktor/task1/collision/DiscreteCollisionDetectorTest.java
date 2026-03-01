package com.viktor.task1.collision;

import com.viktor.task1.exception.AlgorithmException;
import com.viktor.task1.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class DiscreteCollisionDetectorTest {

    private DiscreteCollisionDetector detector;

    @BeforeEach
    void setUp() {
        detector = new DiscreteCollisionDetector(10000);
    }

    @Test
    void shouldReturnCorrectName() {
        assertThat(detector.getName(), equalTo("Discrete"));
    }

    @Test
    void shouldReturnSteps() {
        assertEquals(10000, detector.getSteps());
    }

    @Test
    void shouldThrowForZeroSteps() {
        assertThrows(AlgorithmException.class, () -> new DiscreteCollisionDetector(0));
    }

    @Test
    void shouldThrowForNegativeSteps() {
        assertThrows(AlgorithmException.class, () -> new DiscreteCollisionDetector(-5));
    }

    @Test
    void shouldDetectCircleCircleCollision() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 1, 0, 0, 20);
        MovingObject b = new MovingObject(2, new Circle(20, 0, 5), -1, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
        assertThat(result.time(), closeTo(5.0, 0.5));
    }

    @Test
    void shouldDetectNoCollisionCircleCircle() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 1), 0, 1, 0, 10);
        MovingObject b = new MovingObject(2, new Circle(100, 0, 1), 0, -1, 0, 10);
        CollisionResult result = detector.detect(a, b);
        assertFalse(result.collision());
    }

    @Test
    void shouldDetectCollisionAtStartOverlapping() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 0, 0, 0, 10);
        MovingObject b = new MovingObject(2, new Circle(3, 0, 5), 0, 0, 0, 10);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
        assertThat(result.time(), closeTo(0.0, 0.1));
    }

    @Test
    void shouldDetectAABBAABBCollision() {
        MovingObject a = new MovingObject(1, new AABB(0, 0, 4, 4), 1, 0, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(10, 0, 14, 4), -1, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
        assertThat(result.time(), closeTo(3.0, 0.5));
    }

    @Test
    void shouldDetectNoCollisionAABBAABB() {
        MovingObject a = new MovingObject(1, new AABB(0, 0, 2, 2), 0, 1, 0, 10);
        MovingObject b = new MovingObject(2, new AABB(100, 100, 102, 102), 0, -1, 0, 10);
        CollisionResult result = detector.detect(a, b);
        assertFalse(result.collision());
    }

    @Test
    void shouldDetectCircleAABBCollision() {
        MovingObject a = new MovingObject(1, new Circle(0, 2, 2), 1, 0, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(8, 0, 12, 4), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
    }

    @Test
    void shouldDetectAABBCircleCollision() {
        MovingObject a = new MovingObject(1, new AABB(8, 0, 12, 4), 0, 0, 0, 20);
        MovingObject b = new MovingObject(2, new Circle(0, 2, 2), 1, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
    }

    @Test
    void shouldHandleParallelMovement() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 1), 1, 0, 0, 10);
        MovingObject b = new MovingObject(2, new Circle(0, 50, 1), 1, 0, 0, 10);
        CollisionResult result = detector.detect(a, b);
        assertFalse(result.collision());
    }

    @Test
    void shouldHandleZeroVelocity() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 1), 0, 0, 0, 10);
        MovingObject b = new MovingObject(2, new Circle(50, 0, 1), 0, 0, 0, 10);
        CollisionResult result = detector.detect(a, b);
        assertFalse(result.collision());
    }

    @Test
    void shouldReturnNoCollisionForNonOverlappingTimes() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 1, 0, 0, 5);
        MovingObject b = new MovingObject(2, new Circle(20, 0, 5), -1, 0, 10, 20);
        CollisionResult result = detector.detect(a, b);
        assertFalse(result.collision());
    }

    @Test
    void shouldThrowForNullObjectA() {
        MovingObject b = new MovingObject(2, new Circle(0, 0, 1), 0, 0, 0, 10);
        assertThrows(AlgorithmException.class, () -> detector.detect(null, b));
    }

    @Test
    void shouldThrowForNullObjectB() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 1), 0, 0, 0, 10);
        assertThrows(AlgorithmException.class, () -> detector.detect(a, null));
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0, 5, 1, 0, 30, 0, 5, -1, 0, true",
            "0, 0, 1, 0, 0, 100, 0, 1, 0, 0, false",
            "0, 0, 5, 1, 0, 0, 10, 5, -1, 0, true"
    })
    void shouldDetectCollisionParameterized(
            double ax, double ay, double ar, double avx, double avy,
            double bx, double by, double br, double bvx, double bvy,
            boolean expectedCollision) {
        MovingObject a = new MovingObject(1, new Circle(ax, ay, ar), avx, avy, 0, 20);
        MovingObject b = new MovingObject(2, new Circle(bx, by, br), bvx, bvy, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertEquals(expectedCollision, result.collision());
    }

    @Test
    void shouldHandleIdenticalTrajectories() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 1, 0, 0, 10);
        MovingObject b = new MovingObject(2, new Circle(0, 0, 5), 1, 0, 0, 10);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
    }

    @Test
    void shouldWorkWithLowStepCount() {
        DiscreteCollisionDetector lowRes = new DiscreteCollisionDetector(5);
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 1, 0, 0, 20);
        MovingObject b = new MovingObject(2, new Circle(20, 0, 5), -1, 0, 0, 20);
        CollisionResult result = lowRes.detect(a, b);
        assertTrue(result.collision());
    }

    @Test
    void assumptionTrueTest() {
        assumeTrue(detector.getSteps() > 0);
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 1, 0, 0, 20);
        MovingObject b = new MovingObject(2, new Circle(20, 0, 5), -1, 0, 0, 20);
        assertTrue(detector.detect(a, b).collision());
    }

    @Test
    void assumptionFalseTest() {
        assumeFalse(detector.getSteps() <= 0);
        MovingObject a = new MovingObject(1, new Circle(0, 0, 1), 0, 0, 0, 10);
        MovingObject b = new MovingObject(2, new Circle(50, 0, 1), 0, 0, 0, 10);
        assertFalse(detector.detect(a, b).collision());
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> stepCountScenarios() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(10),
                org.junit.jupiter.params.provider.Arguments.of(100),
                org.junit.jupiter.params.provider.Arguments.of(1000),
                org.junit.jupiter.params.provider.Arguments.of(5000)
        );
    }

    @ParameterizedTest
    @MethodSource("stepCountScenarios")
    void shouldDetectWithVariousStepCounts(int stepCount) {
        DiscreteCollisionDetector d = new DiscreteCollisionDetector(stepCount);
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 1, 0, 0, 20);
        MovingObject b = new MovingObject(2, new Circle(20, 0, 5), -1, 0, 0, 20);
        CollisionResult result = d.detect(a, b);
        assertTrue(result.collision());
    }
}

