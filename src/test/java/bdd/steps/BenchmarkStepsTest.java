package bdd.steps;

import bdd.config.BddContext;
import benchmark.experiment.ExperimentResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BenchmarkStepsTest {

    @TempDir
    Path tempDir;

    @AfterEach
    void tearDown() {
        BddContext.reset();
    }

    @Test
    void shouldIncludeMaximumDatasetInLoadExperiments() throws IOException {
        BddContext.reset();
        BddContext context = BddContext.current();

        Path smallDataset = createDataset("sparse_small_01.json");
        Path maximumDataset = createDataset("sparse_maximum_01.json");

        context.getLoadDatasets().add(smallDataset);
        context.getLoadDatasets().add(maximumDataset);
        context.getDatasetGenerationTimes().put(smallDataset.getFileName().toString(), 100L);
        context.getDatasetGenerationTimes().put(maximumDataset.getFileName().toString(), 200L);

        BenchmarkSteps steps = new BenchmarkSteps();
        steps.runLoadExperiments(10);

        List<ExperimentResult> results = context.getLoadResults();
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(result -> result.datasetName().equals("sparse_small_01.json")));
        assertTrue(results.stream().anyMatch(result -> result.datasetName().equals("sparse_maximum_01.json")));
    }

    private Path createDataset(String fileName) throws IOException {
        Path file = tempDir.resolve(fileName);
        Files.writeString(file, """
                {
                  "timeStart": 0,
                  "timeEnd": 20,
                  "objects": [
                    {
                      "type": "circle",
                      "x": 0,
                      "y": 0,
                      "vx": 1,
                      "vy": 0,
                      "radius": 5
                    },
                    {
                      "type": "circle",
                      "x": 20,
                      "y": 0,
                      "vx": -1,
                      "vy": 0,
                      "radius": 5
                    }
                  ]
                }
                """);
        return file;
    }
}

