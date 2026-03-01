package com.viktor.task1.service;

import com.viktor.task1.model.CollisionResult;
import com.viktor.task1.model.MovingObject;

import java.util.ArrayList;
import java.util.List;

public class RecommendationService {

    private static final int HIGH_COMPLEXITY_THRESHOLD = 100;
    private static final int PRECISION_STEP_THRESHOLD = 500;

    public List<String> generateRecommendations(List<CollisionResult> results,
                                                List<MovingObject> objects,
                                                String algorithmName,
                                                int steps) {
        List<String> recommendations = new ArrayList<>();

        boolean anyCollision = results.stream().anyMatch(CollisionResult::collision);

        if (!anyCollision) {
            recommendations.add("Столкновений в заданном временном интервале не обнаружено.");
        }

        if (objects.size() > HIGH_COMPLEXITY_THRESHOLD) {
            recommendations.add("Внимание: высокая вычислительная сложность при " + objects.size() +
                    " объектах. Рекомендуется уменьшить количество объектов или использовать аналитический алгоритм.");
        }

        if ("Discrete".equals(algorithmName) && steps < PRECISION_STEP_THRESHOLD) {
            recommendations.add("Рекомендация: увеличьте точность (шаги) для дискретного алгоритма. " +
                    "Текущее количество шагов: " + steps + ". Рекомендуется: >= " + PRECISION_STEP_THRESHOLD + ".");
        }

        if ("Discrete".equals(algorithmName) && objects.size() > 50 && steps > 10000) {
            recommendations.add("Внимание: очень большое количество шагов при большом числе объектов может привести к медленной работе.");
        }

        long collisionCount = results.stream().filter(CollisionResult::collision).count();
        if (collisionCount > 0) {
            recommendations.add("Обнаружено " + collisionCount + " столкновений из " + results.size() + " проверенных пар.");
        }

        return recommendations;
    }
}

