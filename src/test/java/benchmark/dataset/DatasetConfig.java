package benchmark.dataset;

import java.util.Locale;

public record DatasetConfig(
        DatasetType type,
        DatasetSize size,
        int densityLevel,
        double timeStart,
        double timeEnd,
        long seed
) {

    public DatasetConfig {
        if (densityLevel < 0 || densityLevel > 99) {
            throw new IllegalArgumentException("densityLevel must be in range [0, 99]");
        }
    }

    public String fileName() {
        return type.token() + "_" + size.token() + "_" + String.format(Locale.ROOT, "%02d", densityLevel) + ".json";
    }

    public enum DatasetSize {
        SMALL("small", 40),
        LARGE("large", 240),
        MAXIMUM("maximum", 1200);

        private final String token;
        private final int objectCount;

        DatasetSize(String token, int objectCount) {
            this.token = token;
            this.objectCount = objectCount;
        }

        public String token() {
            return token;
        }

        public int objectCount() {
            return objectCount;
        }

        public static DatasetSize fromToken(String token) {
            for (DatasetSize size : values()) {
                if (size.token.equalsIgnoreCase(token)) {
                    return size;
                }
            }
            throw new IllegalArgumentException("Unknown dataset size: " + token);
        }
    }
}

