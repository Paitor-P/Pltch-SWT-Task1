package com.viktor.task1.fuzz;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.viktor.task1.exception.InvalidInputException;
import com.viktor.task1.io.JsonInputLoader;

public class JsonInputLoaderFuzzTarget {

    private static final JsonInputLoader LOADER = new JsonInputLoader();

    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        String payload = data.consumeRemainingAsString();
        try {
            LOADER.loadFromString(payload);
        } catch (InvalidInputException ignored) {
            // InvalidInputException is expected for malformed JSON inputs.
        }
    }
}

