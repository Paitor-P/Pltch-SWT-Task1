package com.viktor.task1.io;

import com.viktor.task1.model.CollisionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

class JsonOutputWriterTest {

    private JsonOutputWriter writer;

    @BeforeEach
    void setUp() {
        writer = new JsonOutputWriter();
    }

    @Test
    void shouldWriteCollisionToString() {
        CollisionResult result = CollisionResult.collisionAt(5.0, 1, 2);
        String json = writer.writeSingleResult(result);

        assertTrue(json.contains("\"collision\" : true"));
        assertTrue(json.contains("\"time\" : 5.0"));
        assertTrue(json.contains("\"objectA\" : 1"));
        assertTrue(json.contains("\"objectB\" : 2"));
    }

    @Test
    void shouldWriteNoCollisionToString() {
        CollisionResult result = CollisionResult.noCollision(3, 4);
        String json = writer.writeSingleResult(result);

        assertTrue(json.contains("\"collision\" : false"));
        assertTrue(json.contains("\"time\" : null"));
    }

    @Test
    void shouldWriteMultipleResults() {
        List<CollisionResult> results = Arrays.asList(
                CollisionResult.collisionAt(3.0, 1, 2),
                CollisionResult.noCollision(1, 3));

        String json = writer.writeToString(results);

        assertTrue(json.contains("\"collision\" : true"));
        assertTrue(json.contains("\"collision\" : false"));
    }

    @Test
    void shouldWriteEmptyResults() {
        String json = writer.writeToString(Collections.emptyList());
        assertThat(json, equalTo("[ ]"));
    }

    @Test
    void shouldWriteToStream() {
        CollisionResult result = CollisionResult.collisionAt(5.0, 1, 2);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        writer.writeToStream(Collections.singletonList(result), baos);

        String output = baos.toString();
        assertTrue(output.contains("\"collision\" : true"));
    }

    @Test
    void shouldWriteToFile(@TempDir Path tempDir) throws Exception {
        Path outputFile = tempDir.resolve("output.json");
        CollisionResult result = CollisionResult.collisionAt(7.5, 1, 2);

        writer.writeToFile(Collections.singletonList(result), outputFile.toString());

        String content = Files.readString(outputFile);
        assertTrue(content.contains("\"collision\" : true"));
        assertTrue(content.contains("7.5"));
    }
}

