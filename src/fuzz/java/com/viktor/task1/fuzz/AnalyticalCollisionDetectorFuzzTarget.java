package com.viktor.task1.fuzz;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.viktor.task1.collision.AnalyticalCollisionDetector;
import com.viktor.task1.exception.InvalidInputException;
import com.viktor.task1.model.AABB;
import com.viktor.task1.model.Circle;
import com.viktor.task1.model.CollisionResult;
import com.viktor.task1.model.MovingObject;
import com.viktor.task1.model.Shape;

public class AnalyticalCollisionDetectorFuzzTarget {

    private static final AnalyticalCollisionDetector DETECTOR = new AnalyticalCollisionDetector();

    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        try {
            MovingObject a = buildObject(data, 1);
            MovingObject b = buildObject(data, 2);
            CollisionResult result = DETECTOR.detect(a, b);
            if (result.collision() && Double.isNaN(result.time())) {
                throw new IllegalStateException("Unexpected NaN collision time");
            }
        } catch (InvalidInputException ignored) {
            // Invalid geometry/time intervals are valid fuzz outcomes.
        }
    }

    private static MovingObject buildObject(FuzzedDataProvider data, int id) {
        Shape shape = buildShape(data);
        double vx = bound(data.consumeRegularDouble(), -1000.0, 1000.0);
        double vy = bound(data.consumeRegularDouble(), -1000.0, 1000.0);
        double t0 = bound(data.consumeRegularDouble(), -10.0, 10.0);
        double dt = Math.abs(bound(data.consumeRegularDouble(), 0.0, 20.0));
        return new MovingObject(id, shape, vx, vy, t0, t0 + dt);
    }

    private static Shape buildShape(FuzzedDataProvider data) {
        if (data.consumeBoolean()) {
            double x = bound(data.consumeRegularDouble(), -100.0, 100.0);
            double y = bound(data.consumeRegularDouble(), -100.0, 100.0);
            double radius = Math.abs(bound(data.consumeRegularDouble(), 0.0, 20.0));
            return new Circle(x, y, radius);
        }

        double x = bound(data.consumeRegularDouble(), -100.0, 100.0);
        double y = bound(data.consumeRegularDouble(), -100.0, 100.0);
        double width = Math.abs(bound(data.consumeRegularDouble(), 0.0, 40.0));
        double height = Math.abs(bound(data.consumeRegularDouble(), 0.0, 40.0));
        double halfW = width / 2.0;
        double halfH = height / 2.0;
        return new AABB(x - halfW, y - halfH, x + halfW, y + halfH);
    }

    private static double bound(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}


