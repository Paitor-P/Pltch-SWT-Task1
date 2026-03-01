package com.viktor.task1.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.viktor.task1.exception.InvalidInputException;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record MovingObject(int id, Shape shape, double vx, double vy, double tStart, double tEnd) {

    @JsonCreator
    public MovingObject(@JsonProperty("id") int id,
                        @JsonProperty("shape") Shape shape,
                        @JsonProperty("vx") double vx,
                        @JsonProperty("vy") double vy,
                        @JsonProperty("tStart") double tStart,
                        @JsonProperty("tEnd") double tEnd) {
        if (shape == null) {
            throw new InvalidInputException("Shape cannot be null");
        }
        if (tStart > tEnd) {
            throw new InvalidInputException("tStart must be <= tEnd: tStart=" + tStart + ", tEnd=" + tEnd);
        }
        this.id = id;
        this.shape = shape;
        this.vx = vx;
        this.vy = vy;
        this.tStart = tStart;
        this.tEnd = tEnd;
    }

    public double getTStart() {
        return tStart;
    }

    public double getTEnd() {
        return tEnd;
    }

    public Vector2D getVelocity() {
        return new Vector2D(vx, vy);
    }

    public Shape getShapeAtTime(double t) {
        double dt = t - tStart;
        Vector2D offset = new Vector2D(vx * dt, vy * dt);
        return shape.translate(offset);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovingObject that = (MovingObject) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    @NotNull
    public String toString() {
        return "MovingObject{id=" + id + ", shape=" + shape + ", v=(" + vx + "," + vy + ")}";
    }
}

