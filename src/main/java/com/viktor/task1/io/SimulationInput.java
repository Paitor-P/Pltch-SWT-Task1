package com.viktor.task1.io;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SimulationInput {

    @JsonProperty("timeStart")
    private double timeStart;

    @JsonProperty("timeEnd")
    private double timeEnd;

    @JsonProperty("objects")
    private List<ObjectEntry> objects;

    public SimulationInput() {
    }

    public SimulationInput(double timeStart, double timeEnd, List<ObjectEntry> objects) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.objects = objects;
    }

    public double getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(double timeStart) {
        this.timeStart = timeStart;
    }

    public double getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(double timeEnd) {
        this.timeEnd = timeEnd;
    }

    public List<ObjectEntry> getObjects() {
        return objects;
    }

    public void setObjects(List<ObjectEntry> objects) {
        this.objects = objects;
    }

    public static class ObjectEntry {

        @JsonProperty("type")
        private String type;

        @JsonProperty("x")
        private double x;

        @JsonProperty("y")
        private double y;

        @JsonProperty("vx")
        private double vx;

        @JsonProperty("vy")
        private double vy;

        @JsonProperty("radius")
        private Double radius;

        @JsonProperty("width")
        private Double width;

        @JsonProperty("height")
        private Double height;

        public ObjectEntry() {
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getVx() {
            return vx;
        }

        public void setVx(double vx) {
            this.vx = vx;
        }

        public double getVy() {
            return vy;
        }

        public void setVy(double vy) {
            this.vy = vy;
        }

        public Double getRadius() {
            return radius;
        }

        public void setRadius(Double radius) {
            this.radius = radius;
        }

        public Double getWidth() {
            return width;
        }

        public void setWidth(Double width) {
            this.width = width;
        }

        public Double getHeight() {
            return height;
        }

        public void setHeight(Double height) {
            this.height = height;
        }
    }
}

