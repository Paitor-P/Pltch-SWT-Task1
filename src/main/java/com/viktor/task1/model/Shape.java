package com.viktor.task1.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Circle.class, name = "circle"),
        @JsonSubTypes.Type(value = AABB.class, name = "aabb")
})
public abstract class Shape {

    public abstract String getType();

    public abstract Vector2D getCenter();

    public abstract Shape translate(Vector2D offset);
}

