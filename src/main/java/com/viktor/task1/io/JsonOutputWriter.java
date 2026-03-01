package com.viktor.task1.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.viktor.task1.exception.InvalidInputException;
import com.viktor.task1.model.CollisionResult;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonOutputWriter {

    private final ObjectMapper objectMapper;

    public JsonOutputWriter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public JsonOutputWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void writeToFile(List<CollisionResult> results, String filePath) {
        try {
            objectMapper.writeValue(new File(filePath), toOutputList(results));
        } catch (IOException e) {
            throw new InvalidInputException("Failed to write JSON to file: " + filePath, e);
        }
    }

    public void writeToStream(List<CollisionResult> results, OutputStream outputStream) {
        try {
            objectMapper.writeValue(outputStream, toOutputList(results));
        } catch (IOException e) {
            throw new InvalidInputException("Failed to write JSON to stream", e);
        }
    }

    public String writeToString(List<CollisionResult> results) {
        try {
            return objectMapper.writeValueAsString(toOutputList(results));
        } catch (IOException e) {
            throw new InvalidInputException("Failed to serialize results to JSON", e);
        }
    }

    public String writeSingleResult(CollisionResult result) {
        try {
            return objectMapper.writeValueAsString(toOutputMap(result));
        } catch (IOException e) {
            throw new InvalidInputException("Failed to serialize result to JSON", e);
        }
    }

    private List<Map<String, Object>> toOutputList(List<CollisionResult> results) {
        List<Map<String, Object>> output = new ArrayList<>();
        for (CollisionResult r : results) {
            output.add(toOutputMap(r));
        }
        return output;
    }

    private Map<String, Object> toOutputMap(CollisionResult r) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("collision", r.collision());
        map.put("time", r.collision() ? r.time() : null);
        map.put("objectA", r.objectA());
        map.put("objectB", r.objectB());
        return map;
    }
}

