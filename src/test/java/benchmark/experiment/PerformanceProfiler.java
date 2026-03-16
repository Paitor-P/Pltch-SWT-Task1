package benchmark.experiment;

import java.util.function.Supplier;

public class PerformanceProfiler {

    public <T> ProfiledValue<T> profile(Supplier<T> operation) {
        Runtime runtime = Runtime.getRuntime();
        long memoryBefore = usedMemory(runtime);
        long start = System.nanoTime();
        T value = operation.get();
        long durationNanos = System.nanoTime() - start;
        long memoryAfter = usedMemory(runtime);
        return new ProfiledValue<>(value, durationNanos, Math.max(0, memoryAfter - memoryBefore));
    }

    public ProfiledValue<Void> profile(Runnable operation) {
        Runtime runtime = Runtime.getRuntime();
        long memoryBefore = usedMemory(runtime);
        long start = System.nanoTime();
        operation.run();
        long durationNanos = System.nanoTime() - start;
        long memoryAfter = usedMemory(runtime);
        return new ProfiledValue<>(null, durationNanos, Math.max(0, memoryAfter - memoryBefore));
    }

    private long usedMemory(Runtime runtime) {
        return runtime.totalMemory() - runtime.freeMemory();
    }

    public record ProfiledValue<T>(T value, long durationNanos, long memoryDeltaBytes) {
    }
}

