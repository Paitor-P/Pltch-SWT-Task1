package com.viktor.task1.io;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

import com.viktor.task1.exception.InvalidInputException;
import com.viktor.task1.model.AABB;
import com.viktor.task1.model.Circle;
import com.viktor.task1.model.MovingObject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class JsonInputLoaderTest {

    private JsonInputLoader loader;

    @BeforeEach
    void setUp() {
        loader = new JsonInputLoader();
    }

    @Test
    void shouldLoadCirclesFromString() {
        String json = "{\"timeStart\":0.0,\"timeEnd\":10.0,\"objects\":[" +
                "{\"type\":\"circle\",\"x\":0,\"y\":0,\"vx\":1,\"vy\":0,\"radius\":5}," +
                "{\"type\":\"circle\",\"x\":20,\"y\":0,\"vx\":-1,\"vy\":0,\"radius\":5}" +
                "]}";

        List<MovingObject> objects = loader.loadFromString(json);

        assertEquals(2, objects.size());
        assertInstanceOf(Circle.class, objects.get(0).shape());
        assertInstanceOf(Circle.class, objects.get(1).shape());
    }

    @Test
    void shouldLoadAABBFromString() {
        String json = "{\"timeStart\":0.0,\"timeEnd\":10.0,\"objects\":[" +
                "{\"type\":\"aabb\",\"x\":0,\"y\":0,\"vx\":1,\"vy\":0,\"width\":4,\"height\":4}" +
                "]}";

        List<MovingObject> objects = loader.loadFromString(json);

        assertEquals(1, objects.size());
        assertInstanceOf(AABB.class, objects.get(0).shape());
    }

    @Test
    void shouldSetCorrectTimeInterval() {
        String json = "{\"timeStart\":2.0,\"timeEnd\":8.0,\"objects\":[" +
                "{\"type\":\"circle\",\"x\":0,\"y\":0,\"vx\":1,\"vy\":0,\"radius\":5}" +
                "]}";

        List<MovingObject> objects = loader.loadFromString(json);

        assertEquals(2.0, objects.get(0).getTStart());
        assertEquals(8.0, objects.get(0).getTEnd());
    }

    @Test
    void shouldSetCorrectVelocity() {
        String json = "{\"timeStart\":0.0,\"timeEnd\":10.0,\"objects\":[" +
                "{\"type\":\"circle\",\"x\":0,\"y\":0,\"vx\":3.5,\"vy\":-2.1,\"radius\":1}" +
                "]}";

        List<MovingObject> objects = loader.loadFromString(json);

        assertEquals(3.5, objects.get(0).vx(), 1e-9);
        assertEquals(-2.1, objects.get(0).vy(), 1e-9);
    }

    @Test
    void shouldAssignSequentialIds() {
        String json = "{\"timeStart\":0.0,\"timeEnd\":10.0,\"objects\":[" +
                "{\"type\":\"circle\",\"x\":0,\"y\":0,\"vx\":0,\"vy\":0,\"radius\":1}," +
                "{\"type\":\"circle\",\"x\":10,\"y\":0,\"vx\":0,\"vy\":0,\"radius\":1}," +
                "{\"type\":\"circle\",\"x\":20,\"y\":0,\"vx\":0,\"vy\":0,\"radius\":1}" +
                "]}";

        List<MovingObject> objects = loader.loadFromString(json);

        assertThat(objects.get(0).id(), equalTo(1));
        assertThat(objects.get(1).id(), equalTo(2));
        assertThat(objects.get(2).id(), equalTo(3));
    }

    @Test
    void shouldThrowForInvalidJson() {
        assertThrows(InvalidInputException.class, () -> loader.loadFromString("not json"));
    }

    @Test
    void shouldThrowForEmptyObjects() {
        String json = "{\"timeStart\":0.0,\"timeEnd\":10.0,\"objects\":[]}";
        assertThrows(InvalidInputException.class, () -> loader.loadFromString(json));
    }

    @Test
    void shouldThrowForNullObjects() {
        String json = "{\"timeStart\":0.0,\"timeEnd\":10.0}";
        assertThrows(InvalidInputException.class, () -> loader.loadFromString(json));
    }

    @Test
    void shouldThrowForInvalidTimeInterval() {
        String json = "{\"timeStart\":10.0,\"timeEnd\":5.0,\"objects\":[" +
                "{\"type\":\"circle\",\"x\":0,\"y\":0,\"vx\":0,\"vy\":0,\"radius\":1}" +
                "]}";
        assertThrows(InvalidInputException.class, () -> loader.loadFromString(json));
    }

    @Test
    void shouldThrowForUnknownType() {
        String json = "{\"timeStart\":0.0,\"timeEnd\":10.0,\"objects\":[" +
                "{\"type\":\"polygon\",\"x\":0,\"y\":0,\"vx\":0,\"vy\":0}" +
                "]}";
        assertThrows(InvalidInputException.class, () -> loader.loadFromString(json));
    }

    @Test
    void shouldThrowForNullType() {
        String json = "{\"timeStart\":0.0,\"timeEnd\":10.0,\"objects\":[" +
                "{\"x\":0,\"y\":0,\"vx\":0,\"vy\":0}" +
                "]}";
        assertThrows(InvalidInputException.class, () -> loader.loadFromString(json));
    }

    @Test
    void shouldThrowForCircleWithoutRadius() {
        String json = "{\"timeStart\":0.0,\"timeEnd\":10.0,\"objects\":[" +
                "{\"type\":\"circle\",\"x\":0,\"y\":0,\"vx\":0,\"vy\":0}" +
                "]}";
        assertThrows(InvalidInputException.class, () -> loader.loadFromString(json));
    }

    @Test
    void shouldLoadFromStream() {
        String json = "{\"timeStart\":0.0,\"timeEnd\":10.0,\"objects\":[" +
                "{\"type\":\"circle\",\"x\":0,\"y\":0,\"vx\":1,\"vy\":0,\"radius\":5}" +
                "]}";
        InputStream stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));

        List<MovingObject> objects = loader.loadFromStream(stream);

        assertEquals(1, objects.size());
    }

    @Test
    void shouldThrowForInvalidStream() {
        InputStream stream = new ByteArrayInputStream("bad".getBytes(StandardCharsets.UTF_8));
        assertThrows(InvalidInputException.class, () -> loader.loadFromStream(stream));
    }

    @Test
    void shouldLoadFromFile(@TempDir Path tempDir) throws IOException {
        String json = "{\"timeStart\":0.0,\"timeEnd\":10.0,\"objects\":[" +
                "{\"type\":\"circle\",\"x\":0,\"y\":0,\"vx\":1,\"vy\":0,\"radius\":5}" +
                "]}";
        Path file = tempDir.resolve("test.json");
        Files.writeString(file, json);

        List<MovingObject> objects = loader.loadFromFile(file.toString());

        assertEquals(1, objects.size());
    }

    @Test
    void shouldThrowForNonExistentFile() {
        assertThrows(InvalidInputException.class,
                () -> loader.loadFromFile("nonexistent_file_12345.json"));
    }

    @Test
    void shouldUseDefaultDimensionsForAABB() {
        String json = "{\"timeStart\":0.0,\"timeEnd\":10.0,\"objects\":[" +
                "{\"type\":\"aabb\",\"x\":5,\"y\":5,\"vx\":0,\"vy\":0}" +
                "]}";

        List<MovingObject> objects = loader.loadFromString(json);

        assertInstanceOf(AABB.class, objects.get(0).shape());
        AABB aabb = (AABB) objects.get(0).shape();
        assertEquals(1.0, aabb.getWidth(), 1e-9);
        assertEquals(1.0, aabb.getHeight(), 1e-9);
    }
}

