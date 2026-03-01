package com.viktor.task1.collision;

import com.viktor.task1.exception.AlgorithmException;
import com.viktor.task1.exception.UnsupportedShapeException;
import com.viktor.task1.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class AnalyticalCollisionDetectorTest {

    private AnalyticalCollisionDetector detector;

    @BeforeEach
    void setUp() {
        detector = new AnalyticalCollisionDetector();
    }

    @Test
    void shouldReturnCorrectName() {
        assertThat(detector.getName(), equalTo("Analytical"));
    }

    @Test
    void shouldDetectCircleCircleCollision() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 1, 0, 0, 20);
        MovingObject b = new MovingObject(2, new Circle(20, 0, 5), -1, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
        assertThat(result.time(), closeTo(5.0, 0.1));
    }

    @Test
    void shouldDetectNoCollisionCircleCircle() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 1), 0, 1, 0, 10);
        MovingObject b = new MovingObject(2, new Circle(100, 0, 1), 0, -1, 0, 10);
        CollisionResult result = detector.detect(a, b);
        assertFalse(result.collision());
    }

    @Test
    void shouldDetectCollisionAtStartCircleCircle() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 1, 0, 0, 10);
        MovingObject b = new MovingObject(2, new Circle(3, 0, 5), -1, 0, 0, 10);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
        assertThat(result.time(), closeTo(0.0, 0.1));
    }

    @Test
    void shouldDetectTangentCollisionCircleCircle() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 1, 0, 0, 20);
        MovingObject b = new MovingObject(2, new Circle(0, 10, 5), 1, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
        assertThat(result.time(), closeTo(0.0, 0.1));
    }

    @Test
    void shouldDetectParallelMovementNoCollision() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 1), 1, 0, 0, 10);
        MovingObject b = new MovingObject(2, new Circle(0, 100, 1), 1, 0, 0, 10);
        CollisionResult result = detector.detect(a, b);
        assertFalse(result.collision());
    }

    @Test
    void shouldDetectZeroVelocityNoCollision() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 1), 0, 0, 0, 10);
        MovingObject b = new MovingObject(2, new Circle(50, 0, 1), 0, 0, 0, 10);
        CollisionResult result = detector.detect(a, b);
        assertFalse(result.collision());
    }

    @Test
    void shouldDetectZeroVelocityOverlapping() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 0, 0, 0, 10);
        MovingObject b = new MovingObject(2, new Circle(3, 0, 5), 0, 0, 0, 10);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
        assertThat(result.time(), closeTo(0.0, 0.1));
    }

    @Test
    void shouldDetectCirclesDifferentBoundaries() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 1, 0, 0, 25);
        MovingObject b = new MovingObject(2, new Circle(20, 0, 5), -1, 0, 5, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
        assertThat(result.time(), closeTo(7.5, 0.1));
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
    void shouldDetectAABBCollisionAtStart() {
        MovingObject a = new MovingObject(1, new AABB(0, 0, 5, 5), 1, 0, 0, 10);
        MovingObject b = new MovingObject(2, new AABB(3, 3, 8, 8), -1, 0, 0, 10);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
        assertThat(result.time(), closeTo(0.0, 0.1));
    }

    @Test
    void shouldDetectAABBParallelNoCollisionY() {
        MovingObject a = new MovingObject(1, new AABB(0, 0, 2, 2), 1, 0, 0, 10);
        MovingObject b = new MovingObject(2, new AABB(0, 100, 2, 102), 1, 0, 0, 10);
        CollisionResult result = detector.detect(a, b);
        assertFalse(result.collision());
    }

    @Test
    void shouldDetectAABBZeroRelativeVelocityOverlap() {
        MovingObject a = new MovingObject(1, new AABB(0, 0, 5, 5), 1, 1, 0, 10);
        MovingObject b = new MovingObject(2, new AABB(3, 3, 8, 8), 1, 1, 0, 10);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
    }

    @Test
    void shouldDetectAABBZeroRelativeVelocityNoOverlap() {
        MovingObject a = new MovingObject(1, new AABB(0, 0, 2, 2), 1, 1, 0, 10);
        MovingObject b = new MovingObject(2, new AABB(10, 10, 12, 12), 1, 1, 0, 10);
        CollisionResult result = detector.detect(a, b);
        assertFalse(result.collision());
    }

    @Test
    void shouldDetectAABBDifferentBoundaries() {
        MovingObject a = new MovingObject(1, new AABB(0, 0, 4, 4), 1, 0, 0, 25);
        MovingObject b = new MovingObject(2, new AABB(10, 0, 14, 4), -1, 0, 5, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
        assertThat(result.time(), closeTo(5.5, 0.1));
    }

    @Test
    void shouldReturnNoCollisionForNonOverlappingTimeIntervals() {
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

    @Test
    void shouldDetectCircleAABBCollision() {
        MovingObject a = new MovingObject(1, new Circle(0, 2, 2), 1, 0, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(8, 0, 12, 4), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
    }

    @Test
    void shouldDetectAABBCircleCollisionReversed() {
        MovingObject a = new MovingObject(1, new AABB(8, 0, 12, 4), 0, 0, 0, 20);
        MovingObject b = new MovingObject(2, new Circle(0, 2, 2), 1, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
    }

    @Test
    void shouldDetectAABBCircleDifferentBoundaries() {
        MovingObject a = new MovingObject(1, new Circle(0, 2, 2), 1, 0, 0, 25);
        MovingObject b = new MovingObject(2, new AABB(8, 0, 12, 4), 0, 0, 5, 20);
        CollisionResult result = detector.detect(a, b);
        System.out.println("res" + result);
        assertTrue(result.collision());
        assertThat(result.time(), closeTo(6, 0.1));
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0, 5, 1, 0, 30, 0, 5, -1, 0, true",
            "0, 0, 1, 0, 0, 100, 0, 1, 0, 0, false",
            "0, 0, 5, 1, 0, 0, 10, 5, -1, 0, true"
    })
    void shouldDetectCircleCollisionParameterized(
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
        assertThat(result.time(), closeTo(0.0, 0.1));
    }

    @Test
    void shouldAssumptionTest() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 1, 0, 0, 10);
        assumeTrue(a.vx() > 0);
        MovingObject b = new MovingObject(2, new Circle(20, 0, 5), -1, 0, 0, 10);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
    }

    static Stream<Arguments> boundaryTimeScenarios() {
        return Stream.of(
                Arguments.of(0.0, 0.0, true),
                Arguments.of(0.0, 0.001, true),
                Arguments.of(0.0, 100.0, true)
        );
    }

    @ParameterizedTest
    @MethodSource("boundaryTimeScenarios")
    void shouldHandleBoundaryTimeIntervals(double tStart, double tEnd, boolean expectedCollision) {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 0, 0, tStart, tEnd);
        MovingObject b = new MovingObject(2, new Circle(3, 0, 5), 0, 0, tStart, tEnd);
        CollisionResult result = detector.detect(a, b);
        assertEquals(expectedCollision, result.collision());
    }

    @Test
    void shouldThrowUnsupportedShapeException() {
        Shape mockShape = Mockito.mock(Shape.class);
        MovingObject a = new MovingObject(1, mockShape, 1, 0, 0, 10);
        MovingObject b = new MovingObject(2, new Circle(0, 2, 2), 1, 0, 0, 20);

        assertThrows(UnsupportedShapeException.class, () -> {
            detector.detect(a, b);
        });
    }

    @Test
    void shouldDetectCircleCircleDiscriminantZero() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 1, 0, 0, 20);
        MovingObject b = new MovingObject(2, new Circle(10, 10, 5), -1, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
    }

    @Test
    void shouldDetectCircleCircleT1NegativeT2InRange() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 2, 0, -5, 20);
        MovingObject b = new MovingObject(2, new Circle(5, 0, 5), 0, 0, -5, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
        assertThat(result.time(), closeTo(-5.0, 0.1));
    }

    @Test
    void shouldDetectCircleCircleBothRootsOutOfRange() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 1), 1, 0, 0, 5);
        MovingObject b = new MovingObject(2, new Circle(100, 0, 1), -1, 0, 0, 5);
        CollisionResult result = detector.detect(a, b);
        assertFalse(result.collision());
    }

    @Test
    void shouldDetectAABBAABBRvxZeroWithXOverlap() {
        MovingObject a = new MovingObject(1, new AABB(0, 0, 4, 4), 0, 1, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(2, 10, 6, 14), 0, -1, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
    }

    @Test
    void shouldDetectAABBAABBRvxZeroWithoutXOverlap() {
        MovingObject a = new MovingObject(1, new AABB(0, 0, 2, 2), 0, 1, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(10, 10, 12, 12), 0, -1, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertFalse(result.collision());
    }

    @Test
    void shouldDetectAABBAABBRvyZeroWithYOverlap() {
        MovingObject a = new MovingObject(1, new AABB(0, 0, 4, 4), 1, 0, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(10, 2, 14, 6), -1, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
    }

    @Test
    void shouldDetectAABBAABBEntryTimeNegativeExitTimePositive() {
        MovingObject a = new MovingObject(1, new AABB(0, 0, 5, 5), 1, 0, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(3, 0, 8, 5), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
        assertThat(result.time(), closeTo(0.0, 0.1));
    }

    @Test
    void shouldDetectAABBAABBCollisionExactlyAtTEnd() {
        MovingObject a = new MovingObject(1, new AABB(0, 0, 2, 2), 1, 0, 0, 10);
        MovingObject b = new MovingObject(2, new AABB(10, 0, 12, 2), -1, 0, 0, 10);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
        assertThat(result.time(), closeTo(4, 0.1));
    }

    @Test
    void shouldDetectCircleAABBCornerCollision() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 2), 1, 1, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(10, 10, 14, 14), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
    }

    @Test
    void shouldDetectCircleAABBLeftEdgeCollision() {
        MovingObject a = new MovingObject(1, new Circle(0, 5, 2), 1, 0, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(10, 0, 14, 10), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
        assertThat(result.time(), closeTo(8.0, 0.1));
    }

    @Test
    void shouldDetectCircleAABBRightEdgeCollision() {
        MovingObject a = new MovingObject(1, new Circle(20, 5, 2), -1, 0, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(6, 0, 10, 10), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
        assertThat(result.time(), closeTo(8.0, 0.1));
    }

    @Test
    void shouldDetectCircleAABBTopEdgeCollision() {
        MovingObject a = new MovingObject(1, new Circle(5, 0, 2), 0, 1, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(0, 10, 10, 14), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
        assertThat(result.time(), closeTo(8.0, 0.1));
    }

    @Test
    void shouldDetectCircleAABBBottomEdgeCollision() {
        MovingObject a = new MovingObject(1, new Circle(5, 20, 2), 0, -1, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(0, 6, 10, 10), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
        assertThat(result.time(), closeTo(8.0, 0.1));
    }

    @Test
    void shouldDetectCircleAABBIsInCornerRegionFalse() {
        MovingObject a = new MovingObject(1, new Circle(0, 5, 2), 1, 0, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(10, 0, 14, 10), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
        assertThat(result.time(), closeTo(8.0, 0.1));
    }

    @Test
    void shouldDetectCircleAABBZeroRelativeVelocityAfterStaticCheck() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 1), 1, 1, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(10, 10, 14, 14), 1, 1, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertFalse(result.collision());
    }

    @Test
    void shouldDetectCircleAABBCollisionJustOutsideTimeWindow() {
        MovingObject a = new MovingObject(1, new Circle(0, 5, 2), 1, 0, 0, 7);
        MovingObject b = new MovingObject(2, new AABB(10, 0, 14, 10), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertFalse(result.collision());
    }

    @Test
    void shouldHandleEpsilonBoundaryForCValue() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 0, 0, 0, 10);
        MovingObject b = new MovingObject(2, new Circle(10 - 1e-10, 0, 5), 0, 0, 0, 10);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
    }

    @Test
    void shouldHandleEpsilonBoundaryForAValue() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 1e-10, 0, 0, 20);
        MovingObject b = new MovingObject(2, new Circle(100, 0, 5), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertFalse(result.collision());
    }

    @Test
    void shouldDetectAABBAABBExitTimeNegative() {
        MovingObject a = new MovingObject(1, new AABB(0, 0, 2, 2), 1, 0, 5, 15);
        MovingObject b = new MovingObject(2, new AABB(10, 0, 12, 2), -1, 0, 0, 4);
        CollisionResult result = detector.detect(a, b);
        assertFalse(result.collision());
    }

    @Test
    void shouldDetectAABBAABBEntryTimeGreaterThanDuration() {
        MovingObject a = new MovingObject(1, new AABB(0, 0, 2, 2), 1, 0, 0, 5);
        MovingObject b = new MovingObject(2, new AABB(100, 0, 102, 2), -1, 0, 0, 5);
        CollisionResult result = detector.detect(a, b);
        assertFalse(result.collision());
    }

    @Test
    void shouldDetectCircleCircleCValueAtEpsilonBoundary() {
        double exactDistance = 10.0 - 1e-10;
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 0, 0, 0, 10);
        MovingObject b = new MovingObject(2, new Circle(exactDistance, 0, 5), 0, 0, 0, 10);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
    }

    @Test
    void shouldDetectCircleCircleAValueAtEpsilonBoundary() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 1e-10, 1e-10, 0, 20);
        MovingObject b = new MovingObject(2, new Circle(100, 0, 5), 1e-10, 1e-10, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertFalse(result.collision());
    }

    @Test
    void shouldDetectCircleCircleT2IsCollisionTime() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 1, 0, 0, 20);
        MovingObject b = new MovingObject(2, new Circle(5, 15, 5), 0, -1, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
        assertThat(result.time(), greaterThan(0.0));
    }

    @Test
    void shouldDetectCircleCircleT1NegativeT2Valid() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 2, 0, -10, 20);
        MovingObject b = new MovingObject(2, new Circle(10, 0, 5), 0, 0, -10, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
        assertThat(result.time(), closeTo(-10.0, 0.1));
    }

    @Test
    void shouldDetectCircleCircleDurationCalculationEdge() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 1, 0, 5, 15);
        MovingObject b = new MovingObject(2, new Circle(20, 0, 5), -1, 0, 10, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
    }

    @Test
    void shouldDetectAABBAABBDtADifferentTStart() {
        MovingObject a = new MovingObject(1, new AABB(0, 0, 4, 4), 1, 0, 5, 20);
        MovingObject b = new MovingObject(2, new AABB(10, 0, 14, 4), -1, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
        assertThat(result.time(), closeTo(5.5, 0.1));
    }

    @Test
    void shouldDetectAABBAABBRvxAtEpsilonBoundary() {
        MovingObject a = new MovingObject(1, new AABB(0, 0, 4, 4), 1e-10, 0, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(10, 0, 14, 4), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertFalse(result.collision());
    }

    @Test
    void shouldDetectAABBAABBT2CalculationForX() {
        MovingObject a = new MovingObject(1, new AABB(10, 0, 14, 4), -1, 0, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(0, 0, 4, 4), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
    }

    @Test
    void shouldDetectAABBAABBRvyAtEpsilonBoundary() {
        MovingObject a = new MovingObject(1, new AABB(0, 0, 4, 4), 0, 1e-10, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(0, 10, 4, 14), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertFalse(result.collision());
    }

    @Test
    void shouldDetectAABBAABBEntryTimeAtEpsilonBoundary() {
        MovingObject a = new MovingObject(1, new AABB(0, 0, 4, 4), 1, 0, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(4 - 1e-10, 0, 8, 4), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
        assertThat(result.time(), closeTo(0.0, 0.1));
    }

    @Test
    void shouldDetectAABBAABBEntryTimeNegativeWithIntersection() {
        MovingObject a = new MovingObject(1, new AABB(2, 2, 6, 6), 0, 0, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(3, 3, 7, 7), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
        assertThat(result.time(), closeTo(0.0, 0.1));
    }

    @Test
    void shouldDetectAABBAABBCollisionTimeAtTEndBoundary() {
        MovingObject a = new MovingObject(1, new AABB(0, 0, 2, 2), 1, 0, 0, 10);
        MovingObject b = new MovingObject(2, new AABB(9.999999999, 0, 11.999999999, 2), -1, 0, 0, 10);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
    }

    @Test
    void shouldDetectAABBAABBNoCollisionFinalReturn() {
        MovingObject a = new MovingObject(1, new AABB(0, 0, 2, 2), 1, 0, 0, 5);
        MovingObject b = new MovingObject(2, new AABB(100, 100, 102, 102), -1, 0, 0, 5);
        CollisionResult result = detector.detect(a, b);
        assertFalse(result.collision());
    }

    @Test
    void shouldDetectCircleAABBDtStartCircleCalculation() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 2), 1, 0, 5, 20);
        MovingObject b = new MovingObject(2, new AABB(10, 0, 14, 4), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
        assertThat(result.time(), closeTo(13.0, 0.1));
    }

    @Test
    void shouldDetectCircleAABBRvAtEpsilonBoundary() {
        MovingObject a = new MovingObject(1, new Circle(0, 2, 2), 1e-10, 1e-10, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(10, 0, 14, 4), 1e-10, 1e-10, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertFalse(result.collision());
    }

    @Test
    void shouldDetectCircleAABBRsquaredCalculation() {
        MovingObject a = new MovingObject(1, new Circle(0, 5, 1), 1, 0, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(10, 0, 14, 10), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
    }

    @Test
    void shouldDetectCircleAABBQuadraticACoefficient() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 2), 1, 1, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(10, 10, 14, 14), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
    }

    @Test
    void shouldDetectCircleAABBCornerRegionFalsePath() {
        MovingObject a = new MovingObject(1, new Circle(0, 5, 2), 1, 0, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(10, 0, 14, 10), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
        assertThat(result.time(), closeTo(8.0, 0.1));
    }

    @Test
    void shouldDetectCircleAABBWallTimeDivision() {
        MovingObject a = new MovingObject(1, new Circle(0, 5, 2), 0.5, 0, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(10, 0, 14, 10), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
        assertThat(result.time(), closeTo(16.0, 0.1));
    }

    @Test
    void shouldDetectCircleAABBTimeRangeAtBoundary() {
        MovingObject a = new MovingObject(1, new Circle(0, 5, 2), 1, 0, 0, 8);
        MovingObject b = new MovingObject(2, new AABB(8 - 1e-10, 0, 12, 10), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
    }

    @Test
    void shouldDetectCircleAABBPyRangeAtBoundary() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 2), 1, 0.5, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(10, 0, 14, 10), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
    }

    @Test
    void shouldDetectCircleAABBPyExactRangeBoundary() {
        MovingObject a = new MovingObject(1, new Circle(0, 0 - 1e-10, 2), 1, 0, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(10, 0, 14, 10), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
    }

    @Test
    void shouldDetectCircleAABBTHitAtDurationBoundary() {
        MovingObject a = new MovingObject(1, new Circle(0, 5, 2), 1, 0, 0, 8);
        MovingObject b = new MovingObject(2, new AABB(8 - 1e-10, 0, 12, 10), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
    }

    @Test
    void shouldDetectCircleAABBNoCollisionFinalReturn() {
        MovingObject a = new MovingObject(1, new Circle(0, 50, 2), 1, 0, 0, 10);
        MovingObject b = new MovingObject(2, new AABB(10, 0, 14, 10), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertFalse(result.collision());
    }

    @Test
    void shouldCheckStaticCircleAABBAtExactDistance() {
        double cx = 10.0;
        double cy = 5.0;
        double r = 5.0;
        double minX = 5.0;
        double maxX = 15.0;
        double minY = 0.0;
        double maxY = 10.0;
        MovingObject a = new MovingObject(1, new Circle(cx - 1e-10, cy, r), 0, 0, 0, 10);
        MovingObject b = new MovingObject(2, new AABB(minX, minY, maxX, maxY), 0, 0, 0, 10);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
    }

    @Test
    void shouldSolveQuadraticLinearCase() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 1e-10, 1, 0, 20);
        MovingObject b = new MovingObject(2, new Circle(0, 10, 5), 1e-10, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
    }

    @Test
    void shouldSolveQuadraticNegativeDiscriminant() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 1), 1, 0, 0, 20);
        MovingObject b = new MovingObject(2, new Circle(0, 100, 1), 0, 1, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertFalse(result.collision());
    }

    @Test
    void shouldSolveQuadraticBothRootsNegative() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), -1, 0, 0, 20);
        MovingObject b = new MovingObject(2, new Circle(-20, 0, 5), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
    }

    @Test
    void shouldSolveQuadraticT1NegativeT2Positive() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 2, 0, -10, 20);
        MovingObject b = new MovingObject(2, new Circle(15, 0, 5), 0, 0, -10, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
    }

    @Test
    void shouldSolveQuadraticFinalReturnInfinity() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 1), 1, 0, 0, 5);
        MovingObject b = new MovingObject(2, new Circle(100, 0, 1), -1, 0, 0, 5);
        CollisionResult result = detector.detect(a, b);
        assertFalse(result.collision());
    }

    @Test
    void shouldIsInCornerRegionBottomLeftCorner() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 2), 1, 1, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(10, 10, 14, 14), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
    }

    @Test
    void shouldIsInCornerRegionTopRightCorner() {
        MovingObject a = new MovingObject(1, new Circle(20, 20, 2), -1, -1, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(10, 10, 14, 14), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
    }

    @Test
    void shouldIsInCornerRegionInsideBoxReturnsFalse() {
        MovingObject a = new MovingObject(1, new Circle(12, 12, 2), 0.1, 0.1, 0, 20);
        MovingObject b = new MovingObject(2, new AABB(10, 10, 14, 14), 0, 0, 0, 20);
        CollisionResult result = detector.detect(a, b);
        assertTrue(result.collision());
        assertThat(result.time(), closeTo(0.0, 0.1));
    }
}

