package com.viktor.task1.model;

import com.viktor.task1.exception.InvalidInputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

class CircleTest {

    @Test
    void shouldCreateCircle() {
        Circle c = new Circle(1.0, 2.0, 5.0);
        assertAll(
                () -> assertEquals(1.0, c.getX()),
                () -> assertEquals(2.0, c.getY()),
                () -> assertEquals(5.0, c.getRadius())
        );
    }

    @Test
    void shouldReturnCircleType() {
        assertThat(new Circle(0, 0, 1).getType(), equalTo("circle"));
    }

    @Test
    void shouldReturnCenter() {
        Circle c = new Circle(3.0, 4.0, 1.0);
        assertEquals(new Vector2D(3.0, 4.0), c.getCenter());
    }

    @Test
    void shouldTranslateCircle() {
        Circle c = new Circle(0, 0, 5);
        Shape translated = c.translate(new Vector2D(3, 4));
        assertTrue(translated instanceof Circle);
        Circle tc = (Circle) translated;
        assertAll(
                () -> assertThat(tc.getX(), closeTo(3.0, 1e-9)),
                () -> assertThat(tc.getY(), closeTo(4.0, 1e-9)),
                () -> assertThat(tc.getRadius(), closeTo(5.0, 1e-9))
        );
    }

    @Test
    void shouldThrowForNegativeRadius() {
        assertThrows(InvalidInputException.class, () -> new Circle(0, 0, -1));
    }

    @Test
    void shouldAllowZeroRadius() {
        Circle c = new Circle(0, 0, 0);
        assertEquals(0.0, c.getRadius());
    }

    @ParameterizedTest
    @CsvSource({
            "0.0, 0.0, 1.0",
            "10.0, 20.0, 0.5",
            "-5.0, -5.0, 100.0"
    })
    void shouldCreateCirclesWithVariousParams(double x, double y, double r) {
        Circle c = new Circle(x, y, r);
        assertThat(c.getRadius(), closeTo(r, 1e-9));
    }

    @Test
    void shouldBeEqualForSameValues() {
        Circle c1 = new Circle(1, 2, 3);
        Circle c2 = new Circle(1, 2, 3);
        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void shouldNotBeEqualForDifferentValues() {
        assertNotEquals(new Circle(1, 2, 3), new Circle(1, 2, 4));
    }

    @Test
    void shouldNotBeEqualToNull() {
        assertFalse(new Circle(0, 0, 1).equals(null));
    }

    @Test
    void shouldHaveToString() {
        String str = new Circle(1, 2, 3).toString();
        assertTrue(str.contains("Circle"));
    }
}

