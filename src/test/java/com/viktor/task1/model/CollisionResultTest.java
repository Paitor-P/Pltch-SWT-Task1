package com.viktor.task1.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CollisionResultTest {

    @Test
    void shouldCreateCollisionResult() {
        CollisionResult r = new CollisionResult(true, 5.0, 1, 2);
        assertAll(
                () -> assertTrue(r.collision()),
                () -> assertEquals(5.0, r.time()),
                () -> assertEquals(1, r.objectA()),
                () -> assertEquals(2, r.objectB())
        );
    }

    @Test
    void shouldCreateNoCollision() {
        CollisionResult r = CollisionResult.noCollision(1, 2);
        assertAll(
                () -> assertFalse(r.collision()),
                () -> assertEquals(-1, r.time()),
                () -> assertEquals(1, r.objectA()),
                () -> assertEquals(2, r.objectB())
        );
    }

    @Test
    void shouldCreateCollisionAt() {
        CollisionResult r = CollisionResult.collisionAt(3.14, 3, 7);
        assertTrue(r.collision());
        assertEquals(3.14, r.time(), 1e-9);
    }

    @Test
    void shouldBeEqualForSameValues() {
        CollisionResult r1 = new CollisionResult(true, 5.0, 1, 2);
        CollisionResult r2 = new CollisionResult(true, 5.0, 1, 2);
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void shouldNotBeEqualForDifferentValues() {
        CollisionResult r1 = new CollisionResult(true, 5.0, 1, 2);
        CollisionResult r2 = new CollisionResult(false, 5.0, 1, 2);
        assertNotEquals(r1, r2);
    }

    @Test
    void shouldHaveToString() {
        CollisionResult r = CollisionResult.collisionAt(1.0, 1, 2);
        assertTrue(r.toString().contains("CollisionResult"));
    }
}

