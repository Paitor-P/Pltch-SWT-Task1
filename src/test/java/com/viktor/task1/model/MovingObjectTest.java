package com.viktor.task1.model;

import com.viktor.task1.exception.InvalidInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MovingObjectTest {

    @Test
    void shouldCreateMovingObject() {
        Circle shape = new Circle(0, 0, 5);
        MovingObject obj = new MovingObject(1, shape, 1.0, 2.0, 0.0, 10.0);
        assertAll(
                () -> assertEquals(1, obj.id()),
                () -> assertEquals(shape, obj.shape()),
                () -> assertEquals(1.0, obj.vx()),
                () -> assertEquals(2.0, obj.vy()),
                () -> assertEquals(0.0, obj.getTStart()),
                () -> assertEquals(10.0, obj.getTEnd())
        );
    }

    @Test
    void shouldReturnVelocityVector() {
        MovingObject obj = new MovingObject(1, new Circle(0, 0, 1), 3.0, 4.0, 0, 10);
        assertEquals(new Vector2D(3.0, 4.0), obj.getVelocity());
    }

    @Test
    void shouldThrowForNullShape() {
        assertThrows(InvalidInputException.class, () -> new MovingObject(1, null, 0, 0, 0, 10));
    }

    @Test
    void shouldThrowForInvalidTimeInterval() {
        assertThrows(InvalidInputException.class,
                () -> new MovingObject(1, new Circle(0, 0, 1), 0, 0, 10, 5));
    }

    @Test
    void shouldGetShapeAtTime() {
        Circle shape = new Circle(0, 0, 5);
        MovingObject obj = new MovingObject(1, shape, 2.0, 0.0, 0.0, 10.0);
        Shape atTime5 = obj.getShapeAtTime(5.0);
        assertTrue(atTime5 instanceof Circle);
        Circle c = (Circle) atTime5;
        assertEquals(10.0, c.getX(), 1e-9);
        assertEquals(0.0, c.getY(), 1e-9);
    }

    @Test
    void shouldGetShapeAtStartTime() {
        Circle shape = new Circle(5, 5, 3);
        MovingObject obj = new MovingObject(1, shape, 1, 1, 0, 10);
        Shape atStart = obj.getShapeAtTime(0);
        Circle c = (Circle) atStart;
        assertEquals(5.0, c.getX(), 1e-9);
        assertEquals(5.0, c.getY(), 1e-9);
    }

    @Test
    void shouldBeEqualById() {
        MovingObject o1 = new MovingObject(1, new Circle(0, 0, 1), 0, 0, 0, 10);
        MovingObject o2 = new MovingObject(1, new Circle(5, 5, 2), 1, 1, 0, 5);
        assertEquals(o1, o2);
        assertEquals(o1.hashCode(), o2.hashCode());
    }

    @Test
    void shouldNotBeEqualForDifferentIds() {
        MovingObject o1 = new MovingObject(1, new Circle(0, 0, 1), 0, 0, 0, 10);
        MovingObject o2 = new MovingObject(2, new Circle(0, 0, 1), 0, 0, 0, 10);
        assertNotEquals(o1, o2);
    }

    @Test
    void shouldHaveToString() {
        MovingObject obj = new MovingObject(1, new Circle(0, 0, 5), 1, 2, 0, 10);
        assertTrue(obj.toString().contains("MovingObject"));
    }
}

