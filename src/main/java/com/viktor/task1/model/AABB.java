package com.viktor.task1.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.viktor.task1.exception.InvalidInputException;

import java.util.Objects;

public final class AABB extends Shape {

    private final double minX;
    private final double minY;
    private final double maxX;
    private final double maxY;

    @JsonCreator
    public AABB(@JsonProperty("minX") double minX,
                @JsonProperty("minY") double minY,
                @JsonProperty("maxX") double maxX,
                @JsonProperty("maxY") double maxY) {
        if (minX > maxX || minY > maxY) {
            throw new InvalidInputException(
                    "AABB min must be <= max: min=(" + minX + "," + minY + "), max=(" + maxX + "," + maxY + ")");
        }
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    @Override
    public String getType() {
        return "aabb";
    }

    @Override
    public Vector2D getCenter() {
        return new Vector2D((minX + maxX) / 2.0, (minY + maxY) / 2.0);
    }

    @Override
    public Shape translate(Vector2D offset) {
        return new AABB(minX + offset.x(), minY + offset.y(),
                maxX + offset.x(), maxY + offset.y());
    }

    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public double getWidth() {
        return maxX - minX;
    }

    public double getHeight() {
        return maxY - minY;
    }

    public boolean intersects(AABB other) {
        return this.minX <= other.maxX && this.maxX >= other.minX &&
                this.minY <= other.maxY && this.maxY >= other.minY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AABB aabb = (AABB) o;
        return Double.compare(aabb.minX, minX) == 0 &&
                Double.compare(aabb.minY, minY) == 0 &&
                Double.compare(aabb.maxX, maxX) == 0 &&
                Double.compare(aabb.maxY, maxY) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minX, minY, maxX, maxY);
    }

    @Override
    public String toString() {
        return "AABB{min=(" + minX + "," + minY + "), max=(" + maxX + "," + maxY + ")}";
    }
}

