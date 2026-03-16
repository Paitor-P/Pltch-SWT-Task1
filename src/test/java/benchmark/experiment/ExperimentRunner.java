package benchmark.experiment;

import com.viktor.task1.collision.AnalyticalCollisionDetector;
import com.viktor.task1.collision.DiscreteCollisionDetector;
import com.viktor.task1.io.JsonInputLoader;
import com.viktor.task1.io.JsonOutputWriter;
import com.viktor.task1.model.CollisionResult;
import com.viktor.task1.model.MovingObject;
import com.viktor.task1.service.SimulationService;

import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExperimentRunner {

    private final JsonInputLoader inputLoader;
    private final JsonOutputWriter outputWriter;
    private final PerformanceProfiler profiler;
    private final List<ExperimentResult> results;

    public ExperimentRunner() {
        this.inputLoader = new JsonInputLoader();
        this.outputWriter = new JsonOutputWriter();
        this.profiler = new PerformanceProfiler();
        this.results = new ArrayList<>();
    }

    public ExperimentResult runExperiment(Path datasetPath, int discreteSteps, long datasetGenerationTimeNanos) {
        PerformanceProfiler.ProfiledValue<List<MovingObject>> loadProfile =
                profiler.profile(() -> inputLoader.loadFromFile(datasetPath.toString()));

        SimulationService analyticalService = new SimulationService(new AnalyticalCollisionDetector());
        SimulationService discreteService = new SimulationService(new DiscreteCollisionDetector(discreteSteps));

        PerformanceProfiler.ProfiledValue<List<CollisionResult>> analyticalProfile =
                profiler.profile(() -> analyticalService.runSimulation(loadProfile.value()));

        PerformanceProfiler.ProfiledValue<List<CollisionResult>> discreteProfile =
                profiler.profile(() -> discreteService.runSimulation(loadProfile.value()));

        PerformanceProfiler.ProfiledValue<Void> serializationProfile = profiler.profile(() -> {
            outputWriter.writeToStream(analyticalProfile.value(), OutputStream.nullOutputStream());
            outputWriter.writeToStream(discreteProfile.value(), OutputStream.nullOutputStream());
        });

        long analyticalCollisionCount = analyticalProfile.value().stream().filter(CollisionResult::collision).count();
        long discreteCollisionCount = discreteProfile.value().stream().filter(CollisionResult::collision).count();
        boolean match = compareResults(analyticalProfile.value(), discreteProfile.value());

        ExperimentResult result = new ExperimentResult(
                datasetPath.getFileName().toString(),
                loadProfile.value().size(),
                datasetGenerationTimeNanos,
                loadProfile.durationNanos(),
                analyticalProfile.durationNanos(),
                discreteProfile.durationNanos(),
                analyticalProfile.memoryDeltaBytes(),
                discreteProfile.memoryDeltaBytes(),
                serializationProfile.durationNanos(),
                match,
                analyticalCollisionCount,
                discreteCollisionCount
        );

        results.add(result);
        System.out.println(formatResult(result));
        return result;
    }

    public List<ExperimentResult> getResults() {
        return List.copyOf(results);
    }

    private String formatResult(ExperimentResult result) {
        return """
                EXPERIMENT_RESULT
                  dataset: %s
                  objectCount: %d
                  times:
                    datasetGenerationTimeNanos: %d (%s ms)
                    datasetLoadingTimeNanos: %d (%s ms)
                    analyticalExecutionTimeNanos: %d (%s ms)
                    discreteExecutionTimeNanos: %d (%s ms)
                    resultSerializationTimeNanos: %d (%s ms)
                  memory:
                    analyticalMemoryBytes: %d
                    discreteMemoryBytes: %d
                  consistency:
                    collisionsMatch: %s
                    analyticalCollisionCount: %d
                    discreteCollisionCount: %d
                """.formatted(
                result.datasetName(),
                result.objectCount(),
                result.datasetGenerationTimeNanos(), formatMillis(result.datasetGenerationTimeNanos()),
                result.datasetLoadingTimeNanos(), formatMillis(result.datasetLoadingTimeNanos()),
                result.analyticalExecutionTimeNanos(), formatMillis(result.analyticalExecutionTimeNanos()),
                result.discreteExecutionTimeNanos(), formatMillis(result.discreteExecutionTimeNanos()),
                result.resultSerializationTimeNanos(), formatMillis(result.resultSerializationTimeNanos()),
                result.analyticalMemoryBytes(),
                result.discreteMemoryBytes(),
                result.collisionsMatch(),
                result.analyticalCollisionCount(),
                result.discreteCollisionCount());
    }

    private String formatMillis(long nanos) {
        return String.format(Locale.ROOT, "%.3f", nanos / 1_000_000.0);
    }

    private boolean compareResults(List<CollisionResult> analytical, List<CollisionResult> discrete) {
        if (analytical.size() != discrete.size()) {
            return false;
        }
        for (int i = 0; i < analytical.size(); i++) {
            CollisionResult a = analytical.get(i);
            CollisionResult d = discrete.get(i);
            if (a.objectA() != d.objectA() || a.objectB() != d.objectB()) {
                return false;
            }
            if (a.collision() != d.collision()) {
                return false;
            }
            if (a.collision() && Math.abs(a.time() - d.time()) > 1.0) {
                return false;
            }
        }
        return true;
    }
}

