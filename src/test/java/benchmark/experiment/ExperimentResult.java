package benchmark.experiment;

public record ExperimentResult(
        String datasetName,
        int objectCount,
        long datasetGenerationTimeNanos,
        long datasetLoadingTimeNanos,
        long analyticalExecutionTimeNanos,
        long discreteExecutionTimeNanos,
        long analyticalMemoryBytes,
        long discreteMemoryBytes,
        long resultSerializationTimeNanos,
        boolean collisionsMatch,
        long analyticalCollisionCount,
        long discreteCollisionCount
) {
}

