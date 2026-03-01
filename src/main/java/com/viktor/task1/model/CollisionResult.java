package com.viktor.task1.model;

import org.jetbrains.annotations.NotNull;

public record CollisionResult(boolean collision, double time, int objectA, int objectB) {

    public static CollisionResult noCollision(int objectA, int objectB) {
        return new CollisionResult(false, -1, objectA, objectB);
    }

    public static CollisionResult collisionAt(double time, int objectA, int objectB) {
        return new CollisionResult(true, time, objectA, objectB);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollisionResult that = (CollisionResult) o;
        return collision == that.collision &&
                Double.compare(that.time, time) == 0 &&
                objectA == that.objectA &&
                objectB == that.objectB;
    }

    @NotNull
    @Override
    public String toString() {
        return "CollisionResult{collision=" + collision + ", time=" + time +
                ", objectA=" + objectA + ", objectB=" + objectB + "}";
    }
}

