package com.viktor.task1.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.viktor.task1.exception.InvalidInputException;

import java.util.Objects;

public final class Circle extends Shape {

    private final double x;
    private final double y;
    private final double radius;

    @JsonCreator
    public Circle(@JsonProperty("x") double x,
                  @JsonProperty("y") double y,
                  @JsonProperty("radius") double radius) {
        if (radius < 0) {
            throw new InvalidInputException("Circle radius cannot be negative: " + radius);
        }
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    @Override
    public String getType() {
        return "circle";
    }

    @Override
    public Vector2D getCenter() {
        return new Vector2D(x, y);
    }

    @Override
    public Shape translate(Vector2D offset) {
        return new Circle(x + offset.x(), y + offset.y(), radius);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Circle circle = (Circle) o;
        return Double.compare(circle.x, x) == 0 &&
                Double.compare(circle.y, y) == 0 &&
                Double.compare(circle.radius, radius) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, radius);
    }

    @Override
    public String toString() {
        return "Circle{x=" + x + ", y=" + y + ", radius=" + radius + "}";
    }
}

