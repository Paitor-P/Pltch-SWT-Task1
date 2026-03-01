package com.viktor.task1.collision;

import com.viktor.task1.model.CollisionResult;
import com.viktor.task1.model.MovingObject;

public interface CollisionDetector {

    String getName();

    CollisionResult detect(MovingObject a, MovingObject b);
}

