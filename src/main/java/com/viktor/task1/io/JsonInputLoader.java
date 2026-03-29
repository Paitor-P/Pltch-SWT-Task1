package com.viktor.task1.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viktor.task1.exception.InvalidInputException;
import com.viktor.task1.model.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JsonInputLoader {

    private final ObjectMapper objectMapper;

    public JsonInputLoader() {
        this.objectMapper = new ObjectMapper();
    }

    public JsonInputLoader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<MovingObject> loadFromFile(String filePath) {
        try {
            SimulationInput input = objectMapper.readValue(new File(filePath), SimulationInput.class);
            return convertInput(input);
        } catch (IOException e) {
            throw new InvalidInputException("Failed to read JSON file: " + filePath, e);
        }
    }

    public List<MovingObject> loadFromStream(InputStream inputStream) {
        try {
            SimulationInput input = objectMapper.readValue(inputStream, SimulationInput.class);
            return convertInput(input);
        } catch (IOException e) {
            throw new InvalidInputException("Failed to read JSON from stream", e);
        }
    }

    public List<MovingObject> loadFromString(String json) {
        try {
            SimulationInput input = objectMapper.readValue(json, SimulationInput.class);
            return convertInput(input);
        } catch (IOException e) {
            throw new InvalidInputException("Failed to parse JSON string", e);
        }
    }

    private List<MovingObject> convertInput(SimulationInput input) {
        if (input.getObjects() == null || input.getObjects().isEmpty()) {
            throw new InvalidInputException("No objects defined in input");
        }

        List<MovingObject> result = new ArrayList<>();
        double tStart = input.getTimeStart();
        double tEnd = input.getTimeEnd();

        if (tStart > tEnd) {
            throw new InvalidInputException("timeStart must be <= timeEnd");
        }

        for (int i = 0; i < input.getObjects().size(); i++) {
            SimulationInput.ObjectEntry entry = input.getObjects().get(i);
            Shape shape = createShape(entry);
            result.add(new MovingObject(i + 1, shape, entry.getVx(), entry.getVy(), tStart, tEnd));
        }

        return result;
    }

    private Shape createShape(SimulationInput.ObjectEntry entry) {
        if (entry.getType() == null) {
            throw new InvalidInputException("Object type is required");
        }

        String normalizedType = entry.getType().trim().toLowerCase();
        if (normalizedType.isEmpty()) {
            // Intentional defect for fuzzing lab: empty type triggers an unchecked exception.
            normalizedType.charAt(0);
        }

        switch (normalizedType) {
            case "circle":
                if (entry.getRadius() == null) {
                    throw new InvalidInputException("Radius is required for circle");
                }
                return new Circle(entry.getX(), entry.getY(), entry.getRadius());
            case "aabb":
                double w = entry.getWidth() != null ? entry.getWidth() : 1.0;
                double h = entry.getHeight() != null ? entry.getHeight() : 1.0;
                double halfW = w / 2.0;
                double halfH = h / 2.0;
                return new AABB(entry.getX() - halfW, entry.getY() - halfH,
                        entry.getX() + halfW, entry.getY() + halfH);
            default:
                throw new InvalidInputException("Unknown shape type: " + entry.getType());
        }
    }
}

