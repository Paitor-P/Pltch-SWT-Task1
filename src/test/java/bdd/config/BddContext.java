package bdd.config;

import benchmark.experiment.ExperimentResult;
import com.viktor.task1.model.CollisionResult;
import com.viktor.task1.model.MovingObject;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BddContext {

    private static final ThreadLocal<BddContext> HOLDER = ThreadLocal.withInitial(BddContext::new);

    private double timeStart = 0.0;
    private double timeEnd = 20.0;
    private List<MovingObject> objects = new ArrayList<>();
    private List<CollisionResult> analyticalResults = new ArrayList<>();
    private List<CollisionResult> discreteResults = new ArrayList<>();
    private List<String> recommendations = new ArrayList<>();
    private Path datasetRoot;
    private final Map<String, Path> datasetPaths = new HashMap<>();
    private final Map<String, Long> datasetGenerationTimes = new HashMap<>();
    private final List<Path> loadDatasets = new ArrayList<>();
    private final List<ExperimentResult> loadResults = new ArrayList<>();
    private ExperimentResult lastExperimentResult;
    private CollisionResult pairAnalyticalResult;
    private CollisionResult pairDiscreteResult;
    private long analyticalExecutionNanos;
    private long discreteExecutionNanos;

    public static BddContext current() {
        return HOLDER.get();
    }

    public static void reset() {
        HOLDER.set(new BddContext());
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

    public List<MovingObject> getObjects() {
        return objects;
    }

    public void setObjects(List<MovingObject> objects) {
        this.objects = objects;
    }

    public List<CollisionResult> getAnalyticalResults() {
        return analyticalResults;
    }

    public void setAnalyticalResults(List<CollisionResult> analyticalResults) {
        this.analyticalResults = analyticalResults;
    }

    public List<CollisionResult> getDiscreteResults() {
        return discreteResults;
    }

    public void setDiscreteResults(List<CollisionResult> discreteResults) {
        this.discreteResults = discreteResults;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }

    public Path getDatasetRoot() {
        return datasetRoot;
    }

    public void setDatasetRoot(Path datasetRoot) {
        this.datasetRoot = datasetRoot;
    }

    public Map<String, Path> getDatasetPaths() {
        return datasetPaths;
    }

    public Map<String, Long> getDatasetGenerationTimes() {
        return datasetGenerationTimes;
    }

    public List<Path> getLoadDatasets() {
        return loadDatasets;
    }

    public List<ExperimentResult> getLoadResults() {
        return loadResults;
    }

    public ExperimentResult getLastExperimentResult() {
        return lastExperimentResult;
    }

    public void setLastExperimentResult(ExperimentResult lastExperimentResult) {
        this.lastExperimentResult = lastExperimentResult;
    }

    public CollisionResult getPairAnalyticalResult() {
        return pairAnalyticalResult;
    }

    public void setPairAnalyticalResult(CollisionResult pairAnalyticalResult) {
        this.pairAnalyticalResult = pairAnalyticalResult;
    }

    public CollisionResult getPairDiscreteResult() {
        return pairDiscreteResult;
    }

    public void setPairDiscreteResult(CollisionResult pairDiscreteResult) {
        this.pairDiscreteResult = pairDiscreteResult;
    }

    public long getAnalyticalExecutionNanos() {
        return analyticalExecutionNanos;
    }

    public void setAnalyticalExecutionNanos(long analyticalExecutionNanos) {
        this.analyticalExecutionNanos = analyticalExecutionNanos;
    }

    public long getDiscreteExecutionNanos() {
        return discreteExecutionNanos;
    }

    public void setDiscreteExecutionNanos(long discreteExecutionNanos) {
        this.discreteExecutionNanos = discreteExecutionNanos;
    }
}

