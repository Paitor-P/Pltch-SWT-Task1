package bdd.steps;

import bdd.config.BddContext;
import benchmark.dataset.DatasetConfig;
import benchmark.dataset.DatasetGenerator;
import benchmark.dataset.DatasetType;
import benchmark.experiment.ExperimentResult;
import benchmark.experiment.ExperimentRunner;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BenchmarkSteps {

    private static final boolean INCLUDE_MAXIMUM_IN_LOAD_EXPERIMENTS = true;

    private final DatasetGenerator generator = new DatasetGenerator();
    private final ExperimentRunner experimentRunner = new ExperimentRunner();

    @Given("dataset generation root is {string}")
    public void datasetGenerationRoot(String rootPath) {
        BddContext.current().setDatasetRoot(Path.of(rootPath));
    }

    @Given("a dataset named {string} for type {string} and size {string} is generated")
    public void datasetNamedForTypeAndSizeGenerated(String fileName, String typeToken, String sizeToken) throws IOException {
        BddContext context = BddContext.current();
        Path root = context.getDatasetRoot() == null ? Path.of("datasets") : context.getDatasetRoot();
        int densityLevel = Integer.parseInt(fileName.substring(fileName.length() - 7, fileName.length() - 5));
        DatasetConfig config = new DatasetConfig(
                DatasetType.fromToken(typeToken),
                DatasetConfig.DatasetSize.fromToken(sizeToken),
                densityLevel,
                context.getTimeStart(),
                context.getTimeEnd(),
                42L + densityLevel
        );
        long start = System.nanoTime();
        Path datasetPath = generator.generate(config, root);
        long generationNanos = System.nanoTime() - start;
        assertEquals(fileName, datasetPath.getFileName().toString());
        context.getDatasetPaths().put(fileName, datasetPath);
        context.getDatasetGenerationTimes().put(fileName, generationNanos);
    }

    @When("experiments are executed on that dataset with {int} discrete steps")
    public void experimentsExecutedOnDataset(int discreteSteps) {
        BddContext context = BddContext.current();
        String fileName = context.getDatasetPaths().keySet().stream().findFirst().orElseThrow();
        Path datasetPath = context.getDatasetPaths().get(fileName);
        long generationTime = context.getDatasetGenerationTimes().getOrDefault(fileName, 0L);
        ExperimentResult result = experimentRunner.runExperiment(datasetPath, discreteSteps, generationTime);
        context.setLastExperimentResult(result);
    }

    @Then("both algorithms produce consistent collision pairs")
    public void algorithmsProduceConsistentCollisionPairs() {
        assertTrue(BddContext.current().getLastExperimentResult().collisionsMatch());
    }

    @And("the experiment result is stored")
    public void experimentResultStored() {
        assertThat(experimentRunner.getResults().size(), greaterThan(0));
    }

    @When("I generate load testing datasets")
    public void generateLoadTestingDatasets() throws IOException {
        BddContext context = BddContext.current();
        Path root = context.getDatasetRoot() == null ? Path.of("datasets") : context.getDatasetRoot();
        List<DatasetConfig> configs = List.of(
                new DatasetConfig(DatasetType.SPARSE, DatasetConfig.DatasetSize.SMALL, 1, context.getTimeStart(), context.getTimeEnd(), 1001L),
                new DatasetConfig(DatasetType.SPARSE, DatasetConfig.DatasetSize.LARGE, 1, context.getTimeStart(), context.getTimeEnd(), 1002L),
                new DatasetConfig(DatasetType.SPARSE, DatasetConfig.DatasetSize.MAXIMUM, 1, context.getTimeStart(), context.getTimeEnd(), 1003L),
                new DatasetConfig(DatasetType.DENSE, DatasetConfig.DatasetSize.SMALL, 2, context.getTimeStart(), context.getTimeEnd(), 2001L),
                new DatasetConfig(DatasetType.DENSE, DatasetConfig.DatasetSize.LARGE, 2, context.getTimeStart(), context.getTimeEnd(), 2002L)
        );

        context.getLoadDatasets().clear();
        for (DatasetConfig config : configs) {
            long start = System.nanoTime();
            Path path = generator.generate(config, root);
            long generationNanos = System.nanoTime() - start;
            context.getLoadDatasets().add(path);
            context.getDatasetGenerationTimes().put(path.getFileName().toString(), generationNanos);
        }
    }

    @Then("dataset files are created for types and sizes")
    public void datasetFilesCreatedForTypesAndSizes() {
        List<String> names = new ArrayList<>();
        for (Path path : BddContext.current().getLoadDatasets()) {
            assertTrue(Files.exists(path));
            names.add(path.getFileName().toString());
        }
        assertTrue(names.stream().anyMatch(name -> name.startsWith("sparse_small_")));
        assertTrue(names.stream().anyMatch(name -> name.startsWith("sparse_large_")));
        assertTrue(names.stream().anyMatch(name -> name.startsWith("dense_small_")));
        assertTrue(names.stream().anyMatch(name -> name.startsWith("dense_large_")));
    }

    @And("maximum dataset is generated")
    public void maximumDatasetGenerated() {
        boolean exists = BddContext.current().getLoadDatasets().stream()
                .map(path -> path.getFileName().toString())
                .anyMatch(name -> name.contains("maximum"));
        assertTrue(exists);
    }

    @Given("load testing datasets are available")
    public void loadTestingDatasetsAvailable() throws IOException {
        if (BddContext.current().getLoadDatasets().isEmpty()) {
            generateLoadTestingDatasets();
        }
    }

    @When("I run load experiments with {int} discrete steps")
    public void runLoadExperiments(int discreteSteps) {
        BddContext context = BddContext.current();
        context.getLoadResults().clear();
        for (Path dataset : context.getLoadDatasets()) {
            String name = dataset.getFileName().toString();
            if (!INCLUDE_MAXIMUM_IN_LOAD_EXPERIMENTS
                    && name.contains(DatasetConfig.DatasetSize.MAXIMUM.token())) {
                continue;
            }
            long generationTime = context.getDatasetGenerationTimes().getOrDefault(name, 0L);
            context.getLoadResults().add(experimentRunner.runExperiment(dataset, discreteSteps, generationTime));
        }
    }

    @Then("each load experiment has timing and memory metrics")
    public void eachLoadExperimentHasTimingAndMemoryMetrics() {
        assertThat(BddContext.current().getLoadResults().size(), greaterThanOrEqualTo(4));
        for (ExperimentResult result : BddContext.current().getLoadResults()) {
            assertThat(result.datasetGenerationTimeNanos(), greaterThanOrEqualTo(0L));
            assertThat(result.datasetLoadingTimeNanos(), greaterThan(0L));
            assertThat(result.analyticalExecutionTimeNanos(), greaterThan(0L));
            assertThat(result.discreteExecutionTimeNanos(), greaterThan(0L));
            assertThat(result.resultSerializationTimeNanos(), greaterThan(0L));
            assertThat(result.analyticalMemoryBytes(), greaterThanOrEqualTo(0L));
            assertThat(result.discreteMemoryBytes(), greaterThanOrEqualTo(0L));
        }
    }

    @And("algorithms remain consistent on each tested dataset")
    public void algorithmsRemainConsistentOnEachTestedDataset() {
        for (ExperimentResult result : BddContext.current().getLoadResults()) {
            assertTrue(result.collisionsMatch());
        }
    }
}

