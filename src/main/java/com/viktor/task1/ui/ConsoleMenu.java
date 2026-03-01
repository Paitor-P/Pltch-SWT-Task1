package com.viktor.task1.ui;

import com.viktor.task1.collision.AnalyticalCollisionDetector;
import com.viktor.task1.collision.CollisionDetector;
import com.viktor.task1.collision.DiscreteCollisionDetector;
import com.viktor.task1.exception.InvalidInputException;
import com.viktor.task1.io.JsonInputLoader;
import com.viktor.task1.io.JsonOutputWriter;
import com.viktor.task1.model.*;
import com.viktor.task1.service.RecommendationService;
import com.viktor.task1.service.SimulationService;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {
    private final Scanner scanner;
    private final PrintStream out;
    private final JsonInputLoader inputLoader;
    private final JsonOutputWriter outputWriter;
    private final RecommendationService recommendationService;

    public ConsoleMenu(InputStream inputStream, PrintStream outputStream) {
        this.scanner = new Scanner(inputStream, StandardCharsets.UTF_8);
        this.out = new PrintStream(outputStream, true, StandardCharsets.UTF_8);
        this.inputLoader = new JsonInputLoader();
        this.outputWriter = new JsonOutputWriter();
        this.recommendationService = new RecommendationService();
    }

    public ConsoleMenu(Scanner scanner, PrintStream out,
                       JsonInputLoader inputLoader,
                       JsonOutputWriter outputWriter,
                       RecommendationService recommendationService) {
        this.scanner = scanner;
        this.out = out;
        this.inputLoader = inputLoader;
        this.outputWriter = outputWriter;
        this.recommendationService = recommendationService;
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    handleManualInput();
                    break;
                case "2":
                    handleFileInput();
                    break;
                case "3":
                    running = false;
                    out.println("Выход...");
                    break;
                default:
                    out.println("Неверная опция. Попробуйте снова.");
                    break;
            }
        }
    }

    private void printMainMenu() {
        out.println("=== Непрерывное обнаружение столкновений ===");
        out.println("1. Ввести объекты вручную");
        out.println("2. Загрузить из JSON-файла");
        out.println("3. Выход");
        out.print("Выберите опцию: ");
    }

    void handleManualInput() {
        try {
            List<MovingObject> objects = new ArrayList<>();
            out.print("Введите начальное время: ");
            double tStart = Double.parseDouble(scanner.nextLine().trim());
            out.print("Введите конечное время: ");
            double tEnd = Double.parseDouble(scanner.nextLine().trim());

            out.print("Введите количество объектов: ");
            int count = Integer.parseInt(scanner.nextLine().trim());

            for (int i = 0; i < count; i++) {
                out.println("--- Объект " + (i + 1) + " ---");
                out.print("Тип (circle/aabb): ");
                String type = scanner.nextLine().trim().toLowerCase();

                out.print("Позиция X: ");
                double x = Double.parseDouble(scanner.nextLine().trim());
                out.print("Позиция Y: ");
                double y = Double.parseDouble(scanner.nextLine().trim());
                out.print("Скорость по X: ");
                double vx = Double.parseDouble(scanner.nextLine().trim());
                out.print("Скорость по Y: ");
                double vy = Double.parseDouble(scanner.nextLine().trim());

                Shape shape;
                if ("circle".equals(type)) {
                    out.print("Радиус: ");
                    double radius = Double.parseDouble(scanner.nextLine().trim());
                    shape = new Circle(x, y, radius);
                } else if ("aabb".equals(type)) {
                    out.print("Ширина: ");
                    double w = Double.parseDouble(scanner.nextLine().trim());
                    out.print("Высота: ");
                    double h = Double.parseDouble(scanner.nextLine().trim());
                    shape = new AABB(x - w / 2, y - h / 2, x + w / 2, y + h / 2);
                } else {
                    out.println("Неизвестный тип: " + type);
                    return;
                }

                objects.add(new MovingObject(i + 1, shape, vx, vy, tStart, tEnd));
            }

            runSimulationWithObjects(objects);
        } catch (NumberFormatException e) {
            out.println("Неверный формат числа: " + e.getMessage());
        } catch (InvalidInputException e) {
            out.println("Ошибка ввода: " + e.getMessage());
        }
    }

    void handleFileInput() {
        try {
            out.print("Введите путь к JSON-файлу: ");
            String filePath = scanner.nextLine().trim();
            List<MovingObject> objects = inputLoader.loadFromFile(filePath);
            runSimulationWithObjects(objects);
        } catch (InvalidInputException e) {
            out.println("Ошибка загрузки файла: " + e.getMessage());
        }
    }

    void runSimulationWithObjects(List<MovingObject> objects) {
        out.println("Выберите алгоритм:");
        out.println("1. Аналитический");
        out.println("2. Дискретный");
        out.print("Ваш выбор: ");
        String algChoice = scanner.nextLine().trim();

        int steps = 1000;
        CollisionDetector detector;
        if ("1".equals(algChoice)) {
            detector = new AnalyticalCollisionDetector();
        } else if ("2".equals(algChoice)) {
            out.print("Введите количество шагов (по умолчанию 1000): ");
            String stepsStr = scanner.nextLine().trim();
            if (!stepsStr.isEmpty()) {
                steps = Integer.parseInt(stepsStr);
            }
            detector = new DiscreteCollisionDetector(steps);
        } else {
            out.println("Неверный выбор алгоритма.");
            return;
        }

        SimulationService service = new SimulationService(detector);
        List<CollisionResult> results = service.runSimulation(objects);

        out.println("\n=== Результаты ===");
        for (CollisionResult result : results) {
            if (result.collision()) {
                out.println("Столкновение: Объект " + result.objectA() +
                        " и Объект " + result.objectB() +
                        " во времени " + String.format("%.4f", result.time()));
            } else {
                out.println("Нет столкновения: Объект " + result.objectA() +
                        " и Объект " + result.objectB());
            }
        }

        List<String> recommendations = recommendationService.generateRecommendations(
                results, objects, detector.getName(), steps);
        if (!recommendations.isEmpty()) {
            out.println("\n=== Рекомендации ===");
            for (String rec : recommendations) {
                out.println("- " + rec);
            }
        }

        out.print("\nСохранить результаты в JSON-файл? (y/n): ");
        String saveChoice = scanner.nextLine().trim();
        if ("y".equalsIgnoreCase(saveChoice)) {
            out.print("Введите путь для сохранения файла: ");
            String outputPath = scanner.nextLine().trim();
            outputWriter.writeToFile(results, outputPath);
            out.println("Результаты сохранены в " + outputPath);
        }
    }
}

