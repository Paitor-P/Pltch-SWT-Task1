package com.viktor.task1.service;

import com.viktor.task1.collision.CollisionDetector;
import com.viktor.task1.model.CollisionResult;
import com.viktor.task1.model.MovingObject;

import java.util.ArrayList;
import java.util.List;

public class SimulationService {

    private final CollisionDetector detector;

    public SimulationService(CollisionDetector detector) {
        this.detector = detector;
    }

    public List<CollisionResult> runSimulation(List<MovingObject> objects) {
        List<CollisionResult> results = new ArrayList<>();

        if (objects == null || objects.size() < 2) {
            return results;
        }

        for (int i = 0; i < objects.size(); i++) {
            for (int j = i + 1; j < objects.size(); j++) {
                CollisionResult result = detector.detect(objects.get(i), objects.get(j));
                results.add(result);
            }
        }

        return results;
    }

    public CollisionResult findFirstCollision(List<MovingObject> objects) {
        List<CollisionResult> results = runSimulation(objects);
        return results.stream()
                .filter(CollisionResult::collision)
                .min((r1, r2) -> Double.compare(r1.time(), r2.time()))
                .orElse(null);
    }

    public CollisionDetector getDetector() {
        return detector;
    }
}

