package com.viktor.task1.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import com.viktor.task1.io.JsonInputLoader;
import com.viktor.task1.io.JsonOutputWriter;
import com.viktor.task1.model.Circle;
import com.viktor.task1.model.MovingObject;
import com.viktor.task1.service.RecommendationService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

@ExtendWith(MockitoExtension.class)
class ConsoleMenuTest {

    @Mock
    private JsonInputLoader mockInputLoader;

    @Mock
    private JsonOutputWriter mockOutputWriter;

    @Mock
    private RecommendationService mockRecommendationService;

    @Test
    void shouldExitOnOption3() {
        String input = "3\n";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        ConsoleMenu menu = new ConsoleMenu(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)), ps);
        menu.run();

        String output = baos.toString();
        assertTrue(output.contains("Выход"));
    }

    @Test
    void shouldHandleInvalidOption() {
        String input = "999\n3\n";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        ConsoleMenu menu = new ConsoleMenu(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)), ps);
        menu.run();

        String output = baos.toString();
        assertTrue(output.contains("Неверная опция"));
    }

    @Test
    void shouldHandleManualCircleInput() {
        String input = "0\n10\n2\ncircle\n0\n0\n1\n0\n5\ncircle\n20\n0\n-1\n0\n5\n1\nn\n";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        ConsoleMenu menu = new ConsoleMenu(scanner, ps, mockInputLoader, mockOutputWriter, mockRecommendationService);

        when(mockRecommendationService.generateRecommendations(any(), any(), any(), anyInt()))
                .thenReturn(Collections.emptyList());

        menu.handleManualInput();

        String output = baos.toString();
        assertTrue(output.contains("Результаты"));
    }

    @Test
    void shouldHandleManualAABBInput() {
        String input = "0\n10\n2\naabb\n0\n0\n1\n0\n4\n4\naabb\n10\n0\n-1\n0\n4\n4\n1\nn\n";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        ConsoleMenu menu = new ConsoleMenu(scanner, ps, mockInputLoader, mockOutputWriter, mockRecommendationService);

        when(mockRecommendationService.generateRecommendations(any(), any(), any(), anyInt()))
                .thenReturn(Collections.emptyList());

        menu.handleManualInput();

        String output = baos.toString();
        assertTrue(output.contains("Результаты"));
    }

    @Test
    void shouldHandleUnknownTypeInManualInput() {
        String input = "0\n10\n1\npolygon\n0\n0\n1\n0\n";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        ConsoleMenu menu = new ConsoleMenu(scanner, ps, mockInputLoader, mockOutputWriter, mockRecommendationService);
        menu.handleManualInput();

        String output = baos.toString();
        assertTrue(output.contains("Неизвестный тип"));
    }

    @Test
    void shouldHandleInvalidNumberInManualInput() {
        String input = "abc\n";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        ConsoleMenu menu = new ConsoleMenu(scanner, ps, mockInputLoader, mockOutputWriter, mockRecommendationService);
        menu.handleManualInput();

        String output = baos.toString();
        assertTrue(output.contains("Неверный формат числа"));
    }

    @Test
    void shouldHandleFileInput() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 1, 0, 0, 10);
        MovingObject b = new MovingObject(2, new Circle(20, 0, 5), -1, 0, 0, 10);

        when(mockInputLoader.loadFromFile("test.json")).thenReturn(Arrays.asList(a, b));
        when(mockRecommendationService.generateRecommendations(any(), any(), any(), anyInt()))
                .thenReturn(Collections.emptyList());

        String input = "test.json\n1\nn\n";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        ConsoleMenu menu = new ConsoleMenu(scanner, ps, mockInputLoader, mockOutputWriter, mockRecommendationService);
        menu.handleFileInput();

        verify(mockInputLoader).loadFromFile("test.json");
        String output = baos.toString();
        assertTrue(output.contains("Результаты"));
    }

    @Test
    void shouldHandleInvalidAlgorithmChoice() {
        String input = "0\n10\n2\ncircle\n0\n0\n1\n0\n5\ncircle\n20\n0\n-1\n0\n5\n5\n";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        ConsoleMenu menu = new ConsoleMenu(scanner, ps, mockInputLoader, mockOutputWriter, mockRecommendationService);
        menu.handleManualInput();

        String output = baos.toString();
        assertTrue(output.contains("Неверный выбор алгоритма"));
    }

    @Test
    void shouldHandleDiscreteAlgorithmSelection() {
        String input = "0\n10\n2\ncircle\n0\n0\n1\n0\n5\ncircle\n20\n0\n-1\n0\n5\n2\n500\nn\n";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        ConsoleMenu menu = new ConsoleMenu(scanner, ps, mockInputLoader, mockOutputWriter, mockRecommendationService);

        when(mockRecommendationService.generateRecommendations(any(), any(), any(), anyInt()))
                .thenReturn(Collections.emptyList());

        menu.handleManualInput();

        String output = baos.toString();
        assertTrue(output.contains("Результаты"));
    }

    @Test
    void shouldHandleDiscreteAlgorithmDefaultSteps() {
        String input = "0\n10\n2\ncircle\n0\n0\n1\n0\n5\ncircle\n20\n0\n-1\n0\n5\n2\n\nn\n";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        ConsoleMenu menu = new ConsoleMenu(scanner, ps, mockInputLoader, mockOutputWriter, mockRecommendationService);

        when(mockRecommendationService.generateRecommendations(any(), any(), any(), anyInt()))
                .thenReturn(Collections.emptyList());

        menu.handleManualInput();

        String output = baos.toString();
        assertTrue(output.contains("Результаты"));
    }

    @Test
    void shouldDisplayRecommendations() {
        String input = "0\n10\n2\ncircle\n0\n0\n1\n0\n5\ncircle\n20\n0\n-1\n0\n5\n1\nn\n";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        ConsoleMenu menu = new ConsoleMenu(scanner, ps, mockInputLoader, mockOutputWriter, mockRecommendationService);

        when(mockRecommendationService.generateRecommendations(any(), any(), any(), anyInt()))
                .thenReturn(Collections.singletonList("Test recommendation"));

        menu.handleManualInput();

        String output = baos.toString();
        assertTrue(output.contains("Рекомендации"));
        assertTrue(output.contains("Test recommendation"));
    }

    @Test
    void shouldSaveResultsWhenRequested() {
        MovingObject a = new MovingObject(1, new Circle(0, 0, 5), 1, 0, 0, 10);
        MovingObject b = new MovingObject(2, new Circle(20, 0, 5), -1, 0, 0, 10);

        when(mockInputLoader.loadFromFile("test.json")).thenReturn(Arrays.asList(a, b));
        when(mockRecommendationService.generateRecommendations(any(), any(), any(), anyInt()))
                .thenReturn(Collections.emptyList());

        String input = "test.json\n1\ny\noutput.json\n";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        ConsoleMenu menu = new ConsoleMenu(scanner, ps, mockInputLoader, mockOutputWriter, mockRecommendationService);
        menu.handleFileInput();

        verify(mockOutputWriter).writeToFile(any(), eq("output.json"));
    }
}

