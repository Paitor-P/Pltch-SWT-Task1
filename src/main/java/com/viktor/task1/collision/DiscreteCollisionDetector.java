package com.viktor.task1.collision;

import com.viktor.task1.exception.AlgorithmException;
import com.viktor.task1.exception.UnsupportedShapeException;
import com.viktor.task1.model.*;

public class DiscreteCollisionDetector implements CollisionDetector {

    private static final double EPSILON = 1e-9;
    private final int steps;

    public DiscreteCollisionDetector(int steps) {
        if (steps <= 0) {
            throw new AlgorithmException("Steps must be positive: " + steps);
        }
        this.steps = steps;
    }

    @Override
    public String getName() {
        return "Discrete";
    }

    @Override
    public CollisionResult detect(MovingObject a, MovingObject b) {
        if (a == null || b == null) {
            throw new AlgorithmException("MovingObject cannot be null");
        }

        double tStart = Math.max(a.getTStart(), b.getTStart());
        double tEnd = Math.min(a.getTEnd(), b.getTEnd());

        if (tStart > tEnd) {
            return CollisionResult.noCollision(a.id(), b.id());
        }

        double duration = tEnd - tStart;
        double dt = duration / steps;

        for (int i = 0; i <= steps; i++) {
            double t = tStart + i * dt;
            Shape shapeA = a.getShapeAtTime(t);
            Shape shapeB = b.getShapeAtTime(t);

            if (checkIntersection(shapeA, shapeB)) {
                return CollisionResult.collisionAt(t, a.id(), b.id());
            }
        }

        return CollisionResult.noCollision(a.id(), b.id());
    }

    private boolean checkIntersection(Shape shapeA, Shape shapeB) {
        if (shapeA instanceof Circle && shapeB instanceof Circle) {
            return circlesIntersect((Circle) shapeA, (Circle) shapeB);
        } else if (shapeA instanceof AABB && shapeB instanceof AABB) {
            return ((AABB) shapeA).intersects((AABB) shapeB);
        } else if (shapeA instanceof Circle && shapeB instanceof AABB) {
            return circleAABBIntersect((Circle) shapeA, (AABB) shapeB);
        } else if (shapeA instanceof AABB && shapeB instanceof Circle) {
            return circleAABBIntersect((Circle) shapeB, (AABB) shapeA);
        }

        throw new UnsupportedShapeException(
                "Unsupported shape combination: " + shapeA.getType() + " and " + shapeB.getType());
    }

    private boolean circlesIntersect(Circle a, Circle b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        double distSq = dx * dx + dy * dy;
        double sumR = a.getRadius() + b.getRadius();
        return distSq <= sumR * sumR + EPSILON;
    }

    private boolean circleAABBIntersect(Circle circle, AABB box) {
        double closestX = Math.max(box.getMinX(), Math.min(circle.getX(), box.getMaxX()));
        double closestY = Math.max(box.getMinY(), Math.min(circle.getY(), box.getMaxY()));

        double dx = circle.getX() - closestX;
        double dy = circle.getY() - closestY;
        double distSq = dx * dx + dy * dy;

        return distSq <= circle.getRadius() * circle.getRadius() + EPSILON;
    }

    public int getSteps() {
        return steps;
    }
}

