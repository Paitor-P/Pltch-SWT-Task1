package com.viktor.task1.service;

import com.viktor.task1.collision.CollisionDetector;
import com.viktor.task1.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimulationServiceTest {

    @Mock
    private CollisionDetector mockDetector;

    private SimulationService service;

    @BeforeEach
    void setUp() {
        service = new SimulationService(mockDetector);
    }

    @Test
    void shouldReturnDetector() {
        assertSame(mockDetector, service.getDetector());
    }

    @Test
    void shouldReturnEmptyForNullObjects() {
        List<CollisionResult> results = service.runSimulation(null);
        assertTrue(results.isEmpty());
    }

    @Test
    void shouldReturnEmptyForSingleObject() {
        MovingObject obj = new MovingObject(1, new Circle(0, 0, 5), 1, 0, 0, 10);
        List<CollisionResult> results = service.runSimulation(Collections.singletonList(obj));
        assertTrue(results.isEmpty());
        verify(mockDetector, never()).detect(any(), any());
    }

    @Test
    void shouldCheckAllPairsForTwoObjects() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 1, 0, 0, 10);
        MovingObject b = new MovingObject(2, new Circle(20, 0, 5), -1, 0, 0, 10);

        when(mockDetector.detect(a, b)).thenReturn(CollisionResult.collisionAt(5.0, 1, 2));

        List<CollisionResult> results = service.runSimulation(Arrays.asList(a, b));

        assertEquals(1, results.size());
        assertTrue(results.get(0).collision());
        verify(mockDetector, times(1)).detect(a, b);
    }

    @Test
    void shouldCheckAllPairsForThreeObjects() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 1), 0, 0, 0, 10);
        MovingObject b = new MovingObject(2, new Circle(10, 0, 1), 0, 0, 0, 10);
        MovingObject c = new MovingObject(3, new Circle(20, 0, 1), 0, 0, 0, 10);

        when(mockDetector.detect(any(), any())).thenReturn(CollisionResult.noCollision(0, 0));

        List<CollisionResult> results = service.runSimulation(Arrays.asList(a, b, c));

        assertEquals(3, results.size());
        verify(mockDetector, times(3)).detect(any(), any());
    }

    @Test
    void shouldFindFirstCollision() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 1), 0, 0, 0, 10);
        MovingObject b = new MovingObject(2, new Circle(10, 0, 1), 0, 0, 0, 10);
        MovingObject c = new MovingObject(3, new Circle(20, 0, 1), 0, 0, 0, 10);

        when(mockDetector.detect(a, b)).thenReturn(CollisionResult.collisionAt(5.0, 1, 2));
        when(mockDetector.detect(a, c)).thenReturn(CollisionResult.collisionAt(3.0, 1, 3));
        when(mockDetector.detect(b, c)).thenReturn(CollisionResult.noCollision(2, 3));

        CollisionResult first = service.findFirstCollision(Arrays.asList(a, b, c));

        assertNotNull(first);
        assertTrue(first.collision());
        assertEquals(3.0, first.time(), 1e-9);
        assertEquals(1, first.objectA());
        assertEquals(3, first.objectB());
    }

    @Test
    void shouldReturnNullIfNoCollisions() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 1), 0, 0, 0, 10);
        MovingObject b = new MovingObject(2, new Circle(100, 0, 1), 0, 0, 0, 10);

        when(mockDetector.detect(a, b)).thenReturn(CollisionResult.noCollision(1, 2));

        CollisionResult first = service.findFirstCollision(Arrays.asList(a, b));

        assertNull(first);
    }

    @Test
    void shouldReturnEmptyForEmptyList() {
        List<CollisionResult> results = service.runSimulation(Collections.emptyList());
        assertTrue(results.isEmpty());
    }

    @Test
    void shouldFindFirstCollisionReturnsNullForSingleObject() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 1), 0, 0, 0, 10);
        CollisionResult first = service.findFirstCollision(Collections.singletonList(a));
        assertNull(first);
    }
}

