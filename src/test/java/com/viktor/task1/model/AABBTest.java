package com.viktor.task1.model;

import com.viktor.task1.exception.InvalidInputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

class AABBTest {

    @Test
    void shouldCreateAABB() {
        AABB box = new AABB(0, 0, 10, 10);
        assertAll(
                () -> assertEquals(0.0, box.getMinX()),
                () -> assertEquals(0.0, box.getMinY()),
                () -> assertEquals(10.0, box.getMaxX()),
                () -> assertEquals(10.0, box.getMaxY())
        );
    }

    @Test
    void shouldReturnAABBType() {
        assertThat(new AABB(0, 0, 1, 1).getType(), equalTo("aabb"));
    }

    @Test
    void shouldReturnCenter() {
        AABB box = new AABB(0, 0, 10, 10);
        assertEquals(new Vector2D(5.0, 5.0), box.getCenter());
    }

    @Test
    void shouldComputeWidthAndHeight() {
        AABB box = new AABB(1, 2, 5, 8);
        assertAll(
                () -> assertThat(box.getWidth(), closeTo(4.0, 1e-9)),
                () -> assertThat(box.getHeight(), closeTo(6.0, 1e-9))
        );
    }

    @Test
    void shouldTranslateAABB() {
        AABB box = new AABB(0, 0, 2, 2);
        Shape translated = box.translate(new Vector2D(3, 4));
        assertTrue(translated instanceof AABB);
        AABB tb = (AABB) translated;
        assertAll(
                () -> assertThat(tb.getMinX(), closeTo(3.0, 1e-9)),
                () -> assertThat(tb.getMinY(), closeTo(4.0, 1e-9)),
                () -> assertThat(tb.getMaxX(), closeTo(5.0, 1e-9)),
                () -> assertThat(tb.getMaxY(), closeTo(6.0, 1e-9))
        );
    }

    @Test
    void shouldThrowForInvalidMinMax() {
        assertThrows(InvalidInputException.class, () -> new AABB(10, 0, 0, 10));
    }

    @Test
    void shouldThrowForInvalidMinMaxY() {
        assertThrows(InvalidInputException.class, () -> new AABB(0, 10, 10, 0));
    }

    @Test
    void shouldAllowZeroSizeAABB() {
        AABB box = new AABB(5, 5, 5, 5);
        assertThat(box.getWidth(), closeTo(0.0, 1e-9));
        assertThat(box.getHeight(), closeTo(0.0, 1e-9));
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0, 2, 2, 1, 1, 3, 3, true",
            "0, 0, 1, 1, 2, 2, 3, 3, false",
            "0, 0, 5, 5, 5, 0, 10, 5, true",
            "0, 0, 3, 3, 3, 3, 6, 6, true"
    })
    void shouldDetectIntersection(double minX1, double minY1, double maxX1, double maxY1,
                                   double minX2, double minY2, double maxX2, double maxY2,
                                   boolean expected) {
        AABB a = new AABB(minX1, minY1, maxX1, maxY1);
        AABB b = new AABB(minX2, minY2, maxX2, maxY2);
        assertEquals(expected, a.intersects(b));
    }

    @Test
    void shouldBeEqualForSameValues() {
        AABB a = new AABB(0, 0, 5, 5);
        AABB b = new AABB(0, 0, 5, 5);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void shouldNotBeEqualForDifferentValues() {
        assertNotEquals(new AABB(0, 0, 5, 5), new AABB(0, 0, 5, 6));
    }

    @Test
    void shouldNotBeEqualToNull() {
        assertFalse(new AABB(0, 0, 1, 1).equals(null));
    }

    @Test
    void shouldHaveToString() {
        assertTrue(new AABB(0, 0, 1, 1).toString().contains("AABB"));
    }
}

