package com.sanjuthomas.openslosdk;

import com.sanjuthomas.openslo.Kind;
import com.sanjuthomas.openslo.OpenSloObject;
import com.sanjuthomas.openslo.Version;
import com.sanjuthomas.openslo.internal.ObjectNames;
import com.sanjuthomas.openslo.v1.DataSource;
import com.sanjuthomas.openslo.v1.SLO;
import com.sanjuthomas.openslo.v1.Service;
import com.sanjuthomas.openslo.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenSloSdkTest {

    @Test
    void decodeSingleYamlService() throws IOException {
        String yaml = TestFixtures.read("test_data/decode/single_object.yaml");
        List<OpenSloObject> objects = OpenSloSdk.decode(yaml, ObjectFormat.YAML);

        assertEquals(1, objects.size());
        Service service = assertInstanceOf(Service.class, objects.get(0));
        assertEquals("users-auth", service.getName());
        assertEquals("Users Auth Service", service.getMetadata().getDisplayName());
        assertEquals("Example Service", service.getSpec().getDescription());
    }

    @Test
    void decodeSingleJsonService() throws IOException {
        String json = TestFixtures.read("test_data/decode/single_object.json");
        List<OpenSloObject> objects = OpenSloSdk.decode(json, ObjectFormat.JSON);

        assertEquals(1, objects.size());
        assertInstanceOf(Service.class, objects.get(0));
    }

    @Test
    void decodeFromInputStreamAndReader() throws IOException {
        String yaml = TestFixtures.read("test_data/decode/single_object.yaml");
        byte[] bytes = yaml.getBytes(StandardCharsets.UTF_8);

        List<OpenSloObject> fromStream =
                OpenSloSdk.decode(new ByteArrayInputStream(bytes), ObjectFormat.YAML);
        List<OpenSloObject> fromReader =
                OpenSloSdk.decode(new StringReader(yaml), ObjectFormat.YAML);

        assertEquals(1, fromStream.size());
        assertEquals(1, fromReader.size());
    }

    @Test
    void decodeSequenceOfYamlObjects() throws IOException {
        String yaml = TestFixtures.read("test_data/decode/sequence_of_objects.yaml");
        List<OpenSloObject> objects = OpenSloSdk.decode(yaml, ObjectFormat.YAML);

        assertEquals(2, objects.size());
        assertTrue(objects.stream().allMatch(o -> o instanceof Service));
    }

    @Test
    void decodeSequenceOfJsonObjects() throws IOException {
        String json = TestFixtures.read("test_data/decode/sequence_of_objects.json");
        List<OpenSloObject> objects = OpenSloSdk.decode(json, ObjectFormat.JSON);

        assertEquals(2, objects.size());
        assertTrue(objects.stream().allMatch(o -> o instanceof Service));
    }

    @Test
    void decodeTwoYamlDocuments() throws IOException {
        String yaml = TestFixtures.read("test_data/decode/two_documents.yaml");
        List<OpenSloObject> objects = OpenSloSdk.decode(yaml, ObjectFormat.YAML);

        assertEquals(2, objects.size());
    }

    @Test
    void decodeV1Slos() throws IOException {
        String yaml = TestFixtures.read("test_data/decode/v1_slos.yaml");
        List<OpenSloObject> objects = OpenSloSdk.decode(yaml, ObjectFormat.YAML);

        assertFalse(objects.isEmpty());
        assertTrue(objects.stream().allMatch(o -> o.getKind() == Kind.SLO));
    }

    @Test
    void decodeV2alphaSlo() throws IOException {
        String yaml = TestFixtures.read("test_data/decode/v2alpha_slos.yaml");
        List<OpenSloObject> objects = OpenSloSdk.decode(yaml, ObjectFormat.YAML);

        assertFalse(objects.isEmpty());
        assertEquals(Kind.SLO, objects.get(0).getKind());
    }

    @Test
    void decodeV2alphaDataSource() throws IOException {
        String yaml = TestFixtures.read("test_data/decode/v2alpha_data_source.yaml");
        List<OpenSloObject> objects = OpenSloSdk.decode(yaml, ObjectFormat.YAML);

        assertEquals(1, objects.size());
        assertEquals(Kind.DATA_SOURCE, objects.get(0).getKind());
    }

    @Test
    void encodeToOutputStreamAndWriter() throws IOException {
        String yaml = TestFixtures.read("test_data/encode/v1_slo.yaml");
        List<OpenSloObject> objects = OpenSloSdk.decode(yaml, ObjectFormat.YAML);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OpenSloSdk.encode(out, ObjectFormat.YAML, objects);
        assertFalse(out.toByteArray().length == 0);

        StringWriter writer = new StringWriter();
        OpenSloSdk.encode(writer, ObjectFormat.JSON, objects.get(0));
        assertTrue(writer.toString().contains("\"kind\""));
    }

    @Test
    void encodeVarargs() throws IOException {
        String yaml = TestFixtures.read("test_data/decode/single_object.yaml");
        Service service = (Service) OpenSloSdk.decode(yaml, ObjectFormat.YAML).get(0);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OpenSloSdk.encode(out, ObjectFormat.JSON, service);
        assertTrue(out.toString(StandardCharsets.UTF_8).contains("users-auth"));
    }

    @Test
    void validateDecodedObjects() throws Exception {
        String yaml = TestFixtures.read("test_data/decode/v1_slos.yaml");
        List<OpenSloObject> objects = OpenSloSdk.decode(yaml, ObjectFormat.YAML);
        OpenSloSdk.validate(objects);
    }

    @Test
    void validateAggregatesErrorsWithObjectNames() {
        Service invalid = new Service();
        ValidationException ex = assertThrows(ValidationException.class, () -> OpenSloSdk.validate(invalid));

        assertFalse(ex.getErrors().isEmpty());
        assertNotNull(ex.getErrors().get(0).getName());
        assertEquals("v1.Service", ObjectNames.getObjectName(invalid));
    }

    @Test
    void inlineReferences() throws Exception {
        String yaml = TestFixtures.read("test_data/inline/inputs/v1_slo.yaml");
        List<OpenSloObject> objects = OpenSloSdk.decode(yaml, ObjectFormat.YAML);

        List<OpenSloObject> inlined = new ReferenceInliner(objects).inline();
        List<SLO> slos = OpenSloSdk.filterByType(inlined, SLO.class);

        assertEquals(2, slos.size());
        assertTrue(slos.stream().allMatch(slo -> slo.getSpec().getIndicator() != null));
        assertTrue(slos.stream().allMatch(slo -> slo.getSpec().getIndicatorRef() == null));
    }

    @Test
    void filterByType() throws IOException {
        String yaml = TestFixtures.read("test_data/decode/sequence_of_objects.yaml");
        List<OpenSloObject> objects = OpenSloSdk.decode(yaml, ObjectFormat.YAML);
        List<Service> services = OpenSloSdk.filterByType(objects, Service.class);

        assertEquals(2, services.size());
    }

    @ParameterizedTest
    @CsvSource({
            "openslo/v1alpha, V1ALPHA",
            "openslo/v1, V1",
            "openslo.com/v2alpha, V2ALPHA"
    })
    void versionFromValue(String value, Version expected) {
        assertEquals(expected, Version.fromValue(value));
    }

    @Test
    void versionFromValueRejectsUnknown() {
        assertThrows(IllegalArgumentException.class, () -> Version.fromValue("openslo/v99"));
    }

    @ParameterizedTest
    @CsvSource({
            "SLO, SLO",
            "Service, SERVICE",
            "DataSource, DATA_SOURCE"
    })
    void kindFromValue(String value, Kind expected) {
        assertEquals(expected, Kind.fromValue(value));
    }

    @Test
    void kindFromValueRejectsUnknown() {
        assertThrows(IllegalArgumentException.class, () -> Kind.fromValue("UnknownKind"));
    }

    @Test
    void decodeRejectsMissingApiVersion() {
        String yaml = """
            kind: Service
            metadata:
              name: test
            """;
        assertThrows(IllegalArgumentException.class, () -> OpenSloSdk.decode(yaml, ObjectFormat.YAML));
    }

    @Test
    void decodeRejectsUnsupportedV1alphaKind() {
        String yaml = """
            apiVersion: openslo/v1alpha
            kind: SLI
            metadata:
              name: test
            """;
        assertThrows(IllegalArgumentException.class, () -> OpenSloSdk.decode(yaml, ObjectFormat.YAML));
    }
}
