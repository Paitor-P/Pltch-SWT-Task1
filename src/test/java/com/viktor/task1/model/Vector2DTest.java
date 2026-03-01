package com.viktor.task1.model;

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

class Vector2DTest {

    @Test
    void shouldCreateDefaultVector() {
        Vector2D v = new Vector2D();
        assertAll(
                () -> assertEquals(0.0, v.x()),
                () -> assertEquals(0.0, v.y())
        );
    }

    @Test
    void shouldCreateVectorWithValues() {
        Vector2D v = new Vector2D(3.0, 4.0);
        assertThat(v.x(), equalTo(3.0));
        assertThat(v.y(), equalTo(4.0));
    }

    @ParameterizedTest
    @CsvSource({
            "1.0, 2.0, 3.0, 4.0, 4.0, 6.0",
            "0.0, 0.0, 0.0, 0.0, 0.0, 0.0",
            "-1.0, -2.0, 1.0, 2.0, 0.0, 0.0",
            "100.0, 200.0, -100.0, -200.0, 0.0, 0.0"
    })
    void shouldAddVectors(double x1, double y1, double x2, double y2, double ex, double ey) {
        Vector2D v1 = new Vector2D(x1, y1);
        Vector2D v2 = new Vector2D(x2, y2);
        Vector2D result = v1.add(v2);
        assertAll(
                () -> assertThat(result.x(), closeTo(ex, 1e-9)),
                () -> assertThat(result.y(), closeTo(ey, 1e-9))
        );
    }

    @ParameterizedTest
    @CsvSource({
            "5.0, 3.0, 2.0, 1.0, 3.0, 2.0",
            "0.0, 0.0, 0.0, 0.0, 0.0, 0.0",
            "1.0, 1.0, 2.0, 2.0, -1.0, -1.0"
    })
    void shouldSubtractVectors(double x1, double y1, double x2, double y2, double ex, double ey) {
        Vector2D result = new Vector2D(x1, y1).subtract(new Vector2D(x2, y2));
        assertThat(result.x(), closeTo(ex, 1e-9));
        assertThat(result.y(), closeTo(ey, 1e-9));
    }

    @Test
    void shouldScaleVector() {
        Vector2D v = new Vector2D(2.0, 3.0).scale(2.0);
        assertEquals(new Vector2D(4.0, 6.0), v);
    }

    @Test
    void shouldScaleByZero() {
        Vector2D v = new Vector2D(5.0, 10.0).scale(0.0);
        assertEquals(new Vector2D(0.0, 0.0), v);
    }

    @Test
    void shouldComputeDotProduct() {
        double dot = new Vector2D(1.0, 2.0).dot(new Vector2D(3.0, 4.0));
        assertThat(dot, closeTo(11.0, 1e-9));
    }

    @Test
    void shouldComputeLength() {
        Vector2D v = new Vector2D(3.0, 4.0);
        assertThat(v.length(), closeTo(5.0, 1e-9));
    }

    @Test
    void shouldComputeZeroLength() {
        assertEquals(0.0, new Vector2D(0, 0).length());
    }

    @Test
    void shouldComputeLengthSquared() {
        Vector2D v = new Vector2D(3.0, 4.0);
        assertThat(v.lengthSquared(), closeTo(25.0, 1e-9));
    }

    @Test
    void shouldComputeDistanceTo() {
        Vector2D v1 = new Vector2D(0, 0);
        Vector2D v2 = new Vector2D(3, 4);
        assertThat(v1.distanceTo(v2), closeTo(5.0, 1e-9));
    }

    @Test
    void shouldBeEqualForSameValues() {
        Vector2D v1 = new Vector2D(1.0, 2.0);
        Vector2D v2 = new Vector2D(1.0, 2.0);
        assertEquals(v1, v2);
        assertEquals(v1.hashCode(), v2.hashCode());
    }

    @Test
    void shouldNotBeEqualForDifferentValues() {
        Vector2D v1 = new Vector2D(1.0, 2.0);
        Vector2D v2 = new Vector2D(3.0, 4.0);
        assertNotEquals(v1, v2);
    }

    @Test
    void shouldNotBeEqualToNull() {
        assertNotEquals(null, new Vector2D(1, 2));
    }

    @Test
    void shouldNotBeEqualToDifferentType() {
        assertNotEquals("string", new Vector2D(1, 2));
    }

    @Test
    void shouldHaveToString() {
        Vector2D v = new Vector2D(1.0, 2.0);
        assertTrue(v.toString().contains("1.0"));
        assertTrue(v.toString().contains("2.0"));
    }

    @Test
    void assumptionTrueTest() {
        Vector2D v = new Vector2D(1, 0);
        assumeTrue(v.length() > 0);
        assertThat(v.length(), closeTo(1.0, 1e-9));
    }

    @Test
    void assumptionFalseTest() {
        Vector2D v = new Vector2D(1, 2);
        assumeFalse(v.length() == 0);
        assertTrue(v.length() > 0);
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> boundaryVectors() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(0.0, 0.0, 0.0),
                org.junit.jupiter.params.provider.Arguments.of(1.0, 0.0, 1.0),
                org.junit.jupiter.params.provider.Arguments.of(0.0, 1.0, 1.0),
                org.junit.jupiter.params.provider.Arguments.of(-1.0, 0.0, 1.0),
                org.junit.jupiter.params.provider.Arguments.of(Double.MAX_VALUE, 0.0, Double.MAX_VALUE)
        );
    }

    @ParameterizedTest
    @MethodSource("boundaryVectors")
    void shouldComputeLengthForBoundaryValues(double x, double y, double expectedLength) {
        Vector2D v = new Vector2D(x, y);
        assertThat(v.length(), closeTo(expectedLength, 1e-6));
    }
}

