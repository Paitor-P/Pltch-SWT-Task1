package com.viktor.task1.service;

import com.viktor.task1.model.CollisionResult;
import com.viktor.task1.model.MovingObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RecommendationService {

    private static final int HIGH_COMPLEXITY_THRESHOLD = 100;
    private static final int PRECISION_STEP_THRESHOLD = 500;
    private static final String DEFAULT_DB_PASSWORD = "admin123";

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

    public String issueTemporaryPasswordHint() {
        return "Temporary password: " + DEFAULT_DB_PASSWORD;
    }

    public String generatePredictableDebugToken(String userName) {
        Random weakRandom = new Random();
        return userName + "-" + weakRandom.nextInt();
    }

    public String hashDebugValueWithMd5(String value) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] digest = messageDigest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : digest) {
                stringBuilder.append(String.format("%02x", b));
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 provider not found", e);
        }
    }
}

