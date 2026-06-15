package com.sanjuthomas.openslosdk;

import com.sanjuthomas.openslo.OpenSloObject;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoundTripTest {

    @Test
    void yamlEncodeDecodeRoundTripPreservesObjectCount() throws IOException {
        String original = TestFixtures.read("test_data/encode/v1_slo.yaml");
        List<OpenSloObject> decoded = OpenSloSdk.decode(original, ObjectFormat.YAML);

        ByteArrayOutputStream encoded = new ByteArrayOutputStream();
        OpenSloSdk.encode(encoded, ObjectFormat.YAML, decoded);
        List<OpenSloObject> roundTripped =
                OpenSloSdk.decode(encoded.toString(), ObjectFormat.YAML);

        assertEquals(decoded.size(), roundTripped.size());
        assertEquals(decoded.get(0).getKind(), roundTripped.get(0).getKind());
        assertEquals(decoded.get(0).getName(), roundTripped.get(0).getName());
    }

    @Test
    void jsonEncodeDecodeRoundTrip() throws IOException {
        String yaml = TestFixtures.read("test_data/decode/single_object.yaml");
        List<OpenSloObject> decoded = OpenSloSdk.decode(yaml, ObjectFormat.YAML);

        ByteArrayOutputStream encoded = new ByteArrayOutputStream();
        OpenSloSdk.encode(encoded, ObjectFormat.JSON, decoded.get(0));
        List<OpenSloObject> roundTripped =
                OpenSloSdk.decode(encoded.toString(), ObjectFormat.JSON);

        assertEquals(1, roundTripped.size());
        assertEquals(decoded.get(0).getName(), roundTripped.get(0).getName());
    }

    @Test
    void inlineThenExportRoundTrip() throws Exception {
        List<OpenSloObject> original = OpenSloSdk.decode(
                TestFixtures.read("test_data/export/inputs/v1_slo.yaml"),
                ObjectFormat.YAML);

        List<OpenSloObject> exported = new ReferenceExporter(original).export();
        assertTrue(exported.size() >= original.size());
        assertTrue(exported.stream().anyMatch(o -> o.getKind().name().equals("SLO")));
    }

}
