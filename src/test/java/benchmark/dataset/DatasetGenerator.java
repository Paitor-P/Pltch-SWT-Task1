package benchmark.dataset;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.viktor.task1.io.SimulationInput;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DatasetGenerator {

    private final ObjectMapper objectMapper;

    public DatasetGenerator() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public Path generate(DatasetConfig config, Path rootDirectory) throws IOException {
        Files.createDirectories(rootDirectory);
        Path datasetPath = rootDirectory.resolve(config.fileName());
        SimulationInput input = new SimulationInput(config.timeStart(), config.timeEnd(), createEntries(config));
        objectMapper.writeValue(datasetPath.toFile(), input);
        return datasetPath;
    }

    private List<SimulationInput.ObjectEntry> createEntries(DatasetConfig config) {
        return switch (config.type()) {
            case SPARSE -> createSparseEntries(config);
            case DENSE -> createDenseEntries(config);
        };
    }

    private List<SimulationInput.ObjectEntry> createSparseEntries(DatasetConfig config) {
        int count = config.size().objectCount();
        int columns = (int) Math.ceil(Math.sqrt(count));
        double spacing = 24.0 + config.densityLevel();
        Random random = new Random(config.seed());
        List<SimulationInput.ObjectEntry> entries = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            int row = i / columns;
            int col = i % columns;
            double x = col * spacing;
            double y = row * spacing;
            double vx = random.nextDouble(-0.2, 0.2);
            double vy = random.nextDouble(-0.2, 0.2);
            entries.add(circleEntry(x, y, vx, vy, 1.5));
        }

        return entries;
    }

    private List<SimulationInput.ObjectEntry> createDenseEntries(DatasetConfig config) {
        int count = config.size().objectCount();
        double spread = Math.max(8.0, 20.0 - config.densityLevel() * 0.8);
        Random random = new Random(config.seed());
        List<SimulationInput.ObjectEntry> entries = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            double x = random.nextDouble(-spread, spread);
            double y = random.nextDouble(-spread, spread);
            double direction = i % 2 == 0 ? 1.0 : -1.0;
            double vx = direction * random.nextDouble(0.2, 1.2);
            double vy = -direction * random.nextDouble(0.2, 1.2);
            entries.add(circleEntry(x, y, vx, vy, 2.0));
        }

        return entries;
    }

    private SimulationInput.ObjectEntry circleEntry(double x, double y, double vx, double vy, double radius) {
        SimulationInput.ObjectEntry entry = new SimulationInput.ObjectEntry();
        entry.setType("circle");
        entry.setX(x);
        entry.setY(y);
        entry.setVx(vx);
        entry.setVy(vy);
        entry.setRadius(radius);
        return entry;
    }
}

