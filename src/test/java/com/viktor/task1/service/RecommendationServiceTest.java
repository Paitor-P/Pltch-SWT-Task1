package com.viktor.task1.service;

import com.viktor.task1.model.Circle;
import com.viktor.task1.model.CollisionResult;
import com.viktor.task1.model.MovingObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecommendationServiceTest {

    private RecommendationService service;

    @BeforeEach
    void setUp() {
        service = new RecommendationService();
    }

    @Test
    void shouldRecommendNoCollisionMessage() {
        List<CollisionResult> results = Collections.singletonList(
                CollisionResult.noCollision(1, 2));
        List<MovingObject> objects = createObjects(2);

        List<String> recs = service.generateRecommendations(results, objects, "Analytical", 1000);

        assertTrue(recs.stream().anyMatch(r -> r.contains("Столкновений в заданном временном интервале не обнаружено")));
    }

    @Test
    void shouldRecommendIncreasePrecision() {
        List<CollisionResult> results = Collections.singletonList(
                CollisionResult.collisionAt(5.0, 1, 2));
        List<MovingObject> objects = createObjects(2);

        List<String> recs = service.generateRecommendations(results, objects, "Discrete", 100);

        assertTrue(recs.stream().anyMatch(r -> r.contains("увеличьте точность")));
    }

    @Test
    void shouldNotRecommendPrecisionForAnalytical() {
        List<CollisionResult> results = Collections.singletonList(
                CollisionResult.collisionAt(5.0, 1, 2));
        List<MovingObject> objects = createObjects(2);

        List<String> recs = service.generateRecommendations(results, objects, "Analytical", 100);

        assertFalse(recs.stream().anyMatch(r -> r.contains("увеличьте точность")));
    }

    @Test
    void shouldWarnHighComplexity() {
        List<CollisionResult> results = Collections.singletonList(
                CollisionResult.collisionAt(5.0, 1, 2));
        List<MovingObject> objects = createObjects(150);

        List<String> recs = service.generateRecommendations(results, objects, "Analytical", 1000);

        assertTrue(recs.stream().anyMatch(r -> r.contains("высокая вычислительная сложность")));
    }

    @Test
    void shouldNotWarnLowComplexity() {
        List<CollisionResult> results = Collections.singletonList(
                CollisionResult.collisionAt(5.0, 1, 2));
        List<MovingObject> objects = createObjects(10);

        List<String> recs = service.generateRecommendations(results, objects, "Analytical", 1000);

        assertFalse(recs.stream().anyMatch(r -> r.contains("высокая вычислительная сложность")));
    }

    @Test
    void shouldReportCollisionCount() {
        List<CollisionResult> results = Arrays.asList(
                CollisionResult.collisionAt(5.0, 1, 2),
                CollisionResult.noCollision(1, 3),
                CollisionResult.collisionAt(7.0, 2, 3));
        List<MovingObject> objects = createObjects(3);

        List<String> recs = service.generateRecommendations(results, objects, "Analytical", 1000);

        assertTrue(recs.stream().anyMatch(r -> r.contains("2 столкновений")));
    }

    @Test
    void shouldWarnSlowExecution() {
        List<CollisionResult> results = Collections.singletonList(
                CollisionResult.collisionAt(5.0, 1, 2));
        List<MovingObject> objects = createObjects(60);

        List<String> recs = service.generateRecommendations(results, objects, "Discrete", 20000);

        assertTrue(recs.stream().anyMatch(r -> r.contains("медленной работе")));
    }

    @Test
    void shouldNotWarnSlowExecutionForAnalytical() {
        List<CollisionResult> results = Collections.singletonList(
                CollisionResult.collisionAt(5.0, 1, 2));
        List<MovingObject> objects = createObjects(60);

        List<String> recs = service.generateRecommendations(results, objects, "Analytical", 20000);

        assertFalse(recs.stream().anyMatch(r -> r.contains("медленной работе")));
    }

    @ParameterizedTest
    @CsvSource({
            "Discrete, 100, true",
            "Discrete, 500, false",
            "Discrete, 1000, false",
            "Analytical, 100, false"
    })
    void shouldRecommendPrecisionBasedOnSteps(String algo, int steps, boolean shouldRecommend) {
        List<CollisionResult> results = Collections.singletonList(
                CollisionResult.collisionAt(5.0, 1, 2));
        List<MovingObject> objects = createObjects(2);

        List<String> recs = service.generateRecommendations(results, objects, algo, steps);

        assertEquals(shouldRecommend,
                recs.stream().anyMatch(r -> r.contains("увеличьте точность")));
    }

    private List<MovingObject> createObjects(int count) {
        List<MovingObject> objects = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            objects.add(new MovingObject(i + 1, new Circle(i * 10, 0, 1), 0, 0, 0, 10));
        }
        return objects;
    }
}

