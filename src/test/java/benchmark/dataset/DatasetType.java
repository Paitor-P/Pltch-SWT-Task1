package benchmark.dataset;

public enum DatasetType {
    SPARSE("sparse"),
    DENSE("dense");

    private final String token;

    DatasetType(String token) {
        this.token = token;
    }

    public String token() {
        return token;
    }

    public static DatasetType fromToken(String token) {
        for (DatasetType type : values()) {
            if (type.token.equalsIgnoreCase(token)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown dataset type: " + token);
    }
}

