package com.sanjuthomas.openslosdk;

import com.sanjuthomas.openslo.OpenSloObject;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;

class DecodeAllFixturesTest {

    static Stream<Path> yamlFixtures() throws IOException {
        try (Stream<Path> paths = Files.walk(TestFixtures.path("test_data"))) {
            return paths
                    .filter(path -> path.toString().endsWith(".yaml") || path.toString().endsWith(".yml"))
                    .filter(path -> !path.toString().contains("/outputs/"))
                    .toList()
                    .stream();
        }
    }

    @ParameterizedTest
    @MethodSource("yamlFixtures")
    void decodeYamlFixture(Path fixture) throws IOException {
        String relative = "test_data" + fixture.toString().split("test_data")[1];
        List<OpenSloObject> objects = OpenSloSdk.decode(TestFixtures.read(relative), ObjectFormat.YAML);
        assertFalse(objects.isEmpty());
    }
}
