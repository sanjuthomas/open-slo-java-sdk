package com.sanjuthomas.openslosdk;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

final class TestFixtures {
    private static final Path RESOURCES = Path.of("src/test/resources");

    private TestFixtures() {}

    static String read(String relativePath) throws IOException {
        return Files.readString(RESOURCES.resolve(relativePath));
    }

    static Path path(String relativePath) {
        return RESOURCES.resolve(relativePath);
    }
}
