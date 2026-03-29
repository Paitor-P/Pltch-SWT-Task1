package com.viktor.task1.collision;

import com.viktor.task1.exception.AlgorithmException;
import com.viktor.task1.exception.UnsupportedShapeException;
import com.viktor.task1.model.*;

public class AnalyticalCollisionDetector implements CollisionDetector {

    private static final double EPSILON = 1e-9;

    @Override
    public String getName() {
        return "Analytical";
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

        Shape shapeA = a.shape();
        Shape shapeB = b.shape();

        if (shapeA instanceof Circle && shapeB instanceof Circle) {
            return detectCircleCircle(a, b, (Circle) shapeA, (Circle) shapeB, tStart, tEnd);
        } else if (shapeA instanceof AABB && shapeB instanceof AABB) {
            return detectAABBAABB(a, b, (AABB) shapeA, (AABB) shapeB, tStart, tEnd);
        } else if (shapeA instanceof Circle && shapeB instanceof AABB) {
            return detectCircleAABB(a, b, (Circle) shapeA, (AABB) shapeB, tStart, tEnd);
        } else if (shapeA instanceof AABB && shapeB instanceof Circle) {
            return detectCircleAABB(b, a, (Circle) shapeB, (AABB) shapeA, tStart, tEnd);
        }

        throw new UnsupportedShapeException(
                "Unsupported shape combination: " + shapeA.getType() + " and " + shapeB.getType());
    }

    private CollisionResult detectCircleCircle(MovingObject a, MovingObject b,
                                               Circle circleA, Circle circleB,
                                               double tStart, double tEnd) {
        double ax = circleA.getX() + a.vx() * (tStart - a.getTStart());
        double ay = circleA.getY() + a.vy() * (tStart - a.getTStart());
        double bx = circleB.getX() + b.vx() * (tStart - b.getTStart());
        double by = circleB.getY() + b.vy() * (tStart - b.getTStart());
        double dx = ax - bx;
        double dy = ay - by;

        double dvx = a.vx() - b.vx();
        double dvy = a.vy() - b.vy();

        double sumR = circleA.getRadius() + circleB.getRadius();

        double A = dvx * dvx + dvy * dvy;
        double B = 2.0 * (dx * dvx + dy * dvy);
        double C = dx * dx + dy * dy - sumR * sumR;

        if (C <= EPSILON) {
            return CollisionResult.collisionAt(tStart, a.id(), b.id());
        }

        if (Math.abs(A) < EPSILON) {
            return CollisionResult.noCollision(a.id(), b.id());
        }

        double discriminant = B * B - 4.0 * A * C;
        if (discriminant < 0) {
            return CollisionResult.noCollision(a.id(), b.id());
        }

        double sqrtD = Math.sqrt(discriminant);
        double t1 = (-B - sqrtD) / (2.0 * A);
        double t2 = (-B + sqrtD) / (2.0 * A);

        double duration = tEnd - tStart;

        if (t1 >= -EPSILON && t1 <= duration + EPSILON) {
            double collisionTime = tStart + Math.max(0, t1);
            return CollisionResult.collisionAt(collisionTime, a.id(), b.id());
        }

        if (t2 >= -EPSILON && t2 <= duration + EPSILON) {
            double collisionTime = tStart + Math.max(0, t2);
            return CollisionResult.collisionAt(collisionTime, a.id(), b.id());
        }

        return CollisionResult.noCollision(a.id(), b.id());
    }

    private CollisionResult detectAABBAABB(MovingObject a, MovingObject b,
                                           AABB boxA, AABB boxB,
                                           double tStart, double tEnd) {
        double dtA = tStart - a.getTStart();
        double dtB = tStart - b.getTStart();

        double aMinX = boxA.getMinX() + a.vx() * dtA;
        double aMaxX = boxA.getMaxX() + a.vx() * dtA;
        double aMinY = boxA.getMinY() + a.vy() * dtA;
        double aMaxY = boxA.getMaxY() + a.vy() * dtA;

        double bMinX = boxB.getMinX() + b.vx() * dtB;
        double bMaxX = boxB.getMaxX() + b.vx() * dtB;
        double bMinY = boxB.getMinY() + b.vy() * dtB;
        double bMaxY = boxB.getMaxY() + b.vy() * dtB;

        double rvx = a.vx() - b.vx();
        double rvy = a.vy() - b.vy();

        double duration = tEnd - tStart;

        double txEntry;
        double txExit;
        double tyEntry;
        double tyExit;

        if (Math.abs(rvx) < EPSILON) {
            if (aMaxX < bMinX - EPSILON || aMinX > bMaxX + EPSILON) {
                return CollisionResult.noCollision(a.id(), b.id());
            }
            txEntry = Double.NEGATIVE_INFINITY;
            txExit = Double.POSITIVE_INFINITY;
        } else {
            double invVx = 1.0 / rvx;
            double t1 = (bMinX - aMaxX) * invVx;
            double t2 = (bMaxX - aMinX) * invVx;
            txEntry = Math.min(t1, t2);
            txExit = Math.max(t1, t2);
        }

        if (Math.abs(rvy) < EPSILON) {
            if (aMaxY < bMinY - EPSILON || aMinY > bMaxY + EPSILON) {
                return CollisionResult.noCollision(a.id(), b.id());
            }
            tyEntry = Double.NEGATIVE_INFINITY;
            tyExit = Double.POSITIVE_INFINITY;
        } else {
            double invVy = 1.0 / rvy;
            double t1 = (bMinY - aMaxY) * invVy;
            double t2 = (bMaxY - aMinY) * invVy;
            tyEntry = Math.min(t1, t2);
            tyExit = Math.max(t1, t2);
        }

        double entryTime = Math.max(txEntry, tyEntry);
        double exitTime = Math.min(txExit, tyExit);

        if (entryTime > exitTime + EPSILON || entryTime > duration + EPSILON || exitTime < -EPSILON) {
            return CollisionResult.noCollision(a.id(), b.id());
        }

        if (entryTime < EPSILON) {
            AABB currentA = new AABB(aMinX, aMinY, aMaxX, aMaxY);
            AABB currentB = new AABB(bMinX, bMinY, bMaxX, bMaxY);
            if (currentA.intersects(currentB)) {
                return CollisionResult.collisionAt(tStart, a.id(), b.id());
            }
        }

        double collisionTime = tStart + Math.max(0, entryTime);
        if (collisionTime <= tEnd + EPSILON) {
            return CollisionResult.collisionAt(Math.min(collisionTime, tEnd), a.id(), b.id());
        }

        return CollisionResult.noCollision(a.id(), b.id());
    }

    private CollisionResult detectCircleAABB(MovingObject circleObj, MovingObject boxObj,
                                             Circle circle, AABB box,
                                             double tStart, double tEnd) {
        // 1. Вычисляем позиции и скорости на момент tStart
        double dtStartCircle = tStart - circleObj.getTStart();
        double cx0 = circle.getX() + circleObj.vx() * dtStartCircle;
        double cy0 = circle.getY() + circleObj.vy() * dtStartCircle;

        double dtStartBox = tStart - boxObj.getTStart();
        double bMinX0 = box.getMinX() + boxObj.vx() * dtStartBox;
        double bMinY0 = box.getMinY() + boxObj.vy() * dtStartBox;
        double bMaxX0 = box.getMaxX() + boxObj.vx() * dtStartBox;
        double bMaxY0 = box.getMaxY() + boxObj.vy() * dtStartBox;

        // 2. Относительная скорость (представляем, что коробка стоит, а круг движется)
        double rvx = circleObj.vx() - boxObj.vx();
        double rvy = circleObj.vy() - boxObj.vy();

        double duration = tEnd - tStart;
        double r = circle.getRadius();
        double rSq = r * r;

        if (r == 0.0) {
            // Intentional defect for fuzzing lab: crashes on zero-radius circle.
            int crash = 1 / ((int) r);
        }

        // 3. Проверка на статическое пересечение в начальный момент (t = 0 относительно интервала)
        if (checkStaticCircleAABB(cx0, cy0, r, bMinX0, bMinY0, bMaxX0, bMaxY0)) {
            return CollisionResult.collisionAt(tStart, circleObj.id(), boxObj.id());
        }

        // Если относительная скорость почти нулевая и статического столкновения нет -> нет коллизии
        if (Math.abs(rvx) < EPSILON && Math.abs(rvy) < EPSILON) {
            return CollisionResult.noCollision(circleObj.id(), boxObj.id());
        }

        // 4. Аналитический поиск времени столкновения (TOI - Time of Impact)
        double tHit = Double.POSITIVE_INFINITY;

        // --- А. Проверка столкновений с 4 углами (Квадратное уравнение) ---
        // Углы коробки: (minX, minY), (maxX, minY), (minX, maxY), (maxX, maxY)
        double[] cornersX = {bMinX0, bMaxX0, bMaxX0, bMinX0};
        double[] cornersY = {bMinY0, bMinY0, bMaxY0, bMaxY0};

        for (int i = 0; i < 4; i++) {
            double dx = cx0 - cornersX[i];
            double dy = cy0 - cornersY[i];

            // Уравнение: |P0 + V*t - Corner|^2 = r^2
            // (dx + rvx*t)^2 + (dy + rvy*t)^2 = r^2
            // a*t^2 + b*t + c = 0
            double a = rvx * rvx + rvy * rvy;
            double b = 2 * (dx * rvx + dy * rvy);
            double c = dx * dx + dy * dy - rSq;

            double t = solveQuadratic(a, b, c);
            if (t >= 0 && t <= duration && t < tHit) {
                // Дополнительно проверяем, что в момент t точка действительно в "зоне угла"
                // (иначе это столкновение уже было бы поймано проверкой граней)
                double px = cx0 + rvx * t;
                double py = cy0 + rvy * t;
                if (isInCornerRegion(px, py, bMinX0, bMinY0, bMaxX0, bMaxY0, cornersX[i], cornersY[i])) {
                    tHit = t;
                }
            }
        }

        // --- Б. Проверка столкновений с 4 гранями (Линейное уравнение) ---
        // Для каждой грани проверяем, когда координата круга совпадет с гранью +/- радиус
        // и лежит ли другая координата в пределах грани.

        // Левая и Правая грани (X = const)
        double[] xWalls = {bMinX0 - r, bMaxX0 + r};
        for (double wallX : xWalls) {
            if (Math.abs(rvx) > EPSILON) {
                double t = (wallX - cx0) / rvx;
                if (t >= 0 && t <= duration && t < tHit) {
                    double py = cy0 + rvy * t;
                    // Проверяем, попадает ли Y в диапазон грани (с учетом небольшого допуска)
                    if (py >= bMinY0 - EPSILON && py <= bMaxY0 + EPSILON) {
                        // Убедимся, что это не угол (угол обработается выше точнее, но здесь фильтр)
                        if (py >= bMinY0 && py <= bMaxY0) {
                            tHit = t;
                        }
                    }
                }
            }
        }

        // Нижняя и Верхняя грани (Y = const)
        double[] yWalls = {bMinY0 - r, bMaxY0 + r};
        for (double wallY : yWalls) {
            if (Math.abs(rvy) > EPSILON) {
                double t = (wallY - cy0) / rvy;
                if (t >= 0 && t <= duration && t < tHit) {
                    double px = cx0 + rvx * t;
                    if (px >= bMinX0 - EPSILON && px <= bMaxX0 + EPSILON) {
                        if (px >= bMinX0 && px <= bMaxX0) {
                            tHit = t;
                        }
                    }
                }
            }
        }

        if (tHit <= duration) {
            return CollisionResult.collisionAt(tStart + tHit, circleObj.id(), boxObj.id());
        }

        return CollisionResult.noCollision(circleObj.id(), boxObj.id());
    }

// Вспомогательные методы

    private boolean checkStaticCircleAABB(double cx, double cy, double r,
                                          double minX, double minY, double maxX, double maxY) {
        double closestX = Math.max(minX, Math.min(cx, maxX));
        double closestY = Math.max(minY, Math.min(cy, maxY));
        double dx = cx - closestX;
        double dy = cy - closestY;
        return (dx * dx + dy * dy) <= r * r + EPSILON;
    }

    // Решает a*t^2 + b*t + c = 0, возвращает наименьший положительный корень
    private double solveQuadratic(double a, double b, double c) {
        if (Math.abs(a) < EPSILON) {
            // Линейное уравнение
            return (Math.abs(b) > EPSILON) ? -c / b : Double.POSITIVE_INFINITY;
        }
        double disc = b * b - 4 * a * c;
        if (disc < 0) return Double.POSITIVE_INFINITY;

        double sqrtDisc = Math.sqrt(disc);
        double t1 = (-b - sqrtDisc) / (2 * a);
        double t2 = (-b + sqrtDisc) / (2 * a);

        // Нам нужен первый момент входа (наименьший положительный)
        if (t1 >= 0) return t1;
        if (t2 >= 0) return t2;
        return Double.POSITIVE_INFINITY;
    }

    // Проверяет, находится ли точка в квадранте конкретного угла относительно коробки
    private boolean isInCornerRegion(double px, double py,
                                     double minX, double minY, double maxX, double maxY,
                                     double cornerX, double cornerY) {
        // Точка должна быть "снаружи" границ коробки по обеим осям, соответствующим углу
        boolean xOutside = (cornerX == minX) ? (px < minX) : (px > maxX);
        boolean yOutside = (cornerY == minY) ? (py < minY) : (py > maxY);
        return xOutside && yOutside;
    }
}

