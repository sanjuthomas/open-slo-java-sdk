package com.sanjuthomas.openslosdk;

import com.sanjuthomas.openslo.OpenSloObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectDecoderTest {

    @Test
    void decodeSkipsBlankYamlDocuments() throws IOException {
        String yaml = """
            apiVersion: openslo/v1
            kind: Service
            metadata:
              name: first
            spec:
              description: First

            ---

            apiVersion: openslo/v1
            kind: Service
            metadata:
              name: second
            spec:
              description: Second
            """;
        List<OpenSloObject> objects = OpenSloSdk.decode(yaml, ObjectFormat.YAML);
        assertEquals(2, objects.size());
    }

    @Test
    void decodeYamlArrayDocument() throws IOException {
        String yaml = """
            - apiVersion: openslo/v1
              kind: Service
              metadata:
                name: array-service
              spec:
                description: From array
            """;
        List<OpenSloObject> objects = OpenSloSdk.decode(yaml, ObjectFormat.YAML);
        assertEquals(1, objects.size());
    }

    @Test
    void decodeIgnoresNullAndEmptyDocuments() throws IOException {
        String yaml = """
            ---

            apiVersion: openslo/v1
            kind: Service
            metadata:
              name: kept
            spec:
              description: Kept
            """;
        List<OpenSloObject> objects = OpenSloSdk.decode(yaml, ObjectFormat.YAML);
        assertEquals(1, objects.size());
    }

    @Test
    void decodeJsonArray() throws IOException {
        String json = """
            [
              {
                "apiVersion": "openslo/v1",
                "kind": "Service",
                "metadata": { "name": "json-array" },
                "spec": { "description": "JSON array entry" }
              }
            ]
            """;
        List<OpenSloObject> objects = OpenSloSdk.decode(json, ObjectFormat.JSON);
        assertEquals(1, objects.size());
    }

    @Test
    void encodeYamlRoundTripForService() throws IOException {
        String yaml = TestFixtures.read("test_data/decode/single_object.yaml");
        List<OpenSloObject> decoded = OpenSloSdk.decode(yaml, ObjectFormat.YAML);

        java.io.ByteArrayOutputStream encoded = new java.io.ByteArrayOutputStream();
        OpenSloSdk.encode(encoded, ObjectFormat.YAML, decoded);
        List<OpenSloObject> roundTripped =
                OpenSloSdk.decode(encoded.toString(), ObjectFormat.YAML);

        assertEquals(1, roundTripped.size());
        assertTrue(roundTripped.get(0).getName().contains("users-auth"));
    }
}
