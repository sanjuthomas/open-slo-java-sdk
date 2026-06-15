package com.sanjuthomas.openslosdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanjuthomas.openslo.OpenSloObject;
import com.sanjuthomas.openslo.v1.DataSource;
import com.sanjuthomas.openslo.v1.DataSourceSpec;
import com.sanjuthomas.openslo.v1.Label;
import com.sanjuthomas.openslo.v1.Metadata;
import com.sanjuthomas.openslo.v1.SLO;
import com.sanjuthomas.openslo.v1.SLOSpec;
import com.sanjuthomas.openslo.v1.Service;
import com.sanjuthomas.openslo.v1.ServiceSpec;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class V1ModelCoverageTest {

    @Test
    void decodeAndValidateDataSource() throws Exception {
        String yaml = """
            apiVersion: openslo/v1
            kind: DataSource
            metadata:
              name: prometheus-prod
              labels:
                team: [team, platform]
            spec:
              description: Production Prometheus
              type: Prometheus
              connectionDetails:
                - url: http://prometheus.example.com
            """;
        List<OpenSloObject> objects = OpenSloSdk.decode(yaml, ObjectFormat.YAML);
        DataSource dataSource = (DataSource) objects.get(0);
        assertEquals("prometheus-prod", dataSource.getName());
        assertDoesNotThrow(() -> OpenSloSdk.validate(dataSource));
    }

    @Test
    void exerciseRawMetricTypeFromValue() {
        assertEquals(
                com.sanjuthomas.openslo.v1.SLIRawMetricType.FAILURE,
                com.sanjuthomas.openslo.v1.SLIRawMetricType.fromValue("failure"));
    }

    @Test
    void decodeSloWithDurationShorthandAndCalendar() throws Exception {
        List<SLO> slos = OpenSloSdk.filterByType(
                OpenSloSdk.decode(TestFixtures.read("test_data/inline/inputs/v1_slo.yaml"), ObjectFormat.YAML),
                SLO.class);
        assertDoesNotThrow(() -> OpenSloSdk.validate(slos.get(0)));
        assertNotNull(slos.get(0).getSpec().getTimeWindow());
    }

    @Test
    void buildServiceWithLabelsRoundTrip() throws Exception {
        Metadata metadata = new Metadata("labeled-service");
        Map<String, Label> labels = new HashMap<>();
        labels.put("env", new Label(List.of("env", "production")));
        metadata.setLabels(labels);

        ServiceSpec serviceSpec = new ServiceSpec();
        serviceSpec.setDescription("Labeled service");
        Service service = Service.newService(metadata, serviceSpec);
        ObjectMapper mapper = JacksonConfig.encodeJsonMapper();
        String json = mapper.writeValueAsString(service);
        Service decoded = mapper.readValue(json, Service.class);

        assertNotNull(decoded.getMetadata().getLabels().get("env"));
        assertEquals("production", decoded.getMetadata().getLabels().get("env").get(1));
    }

    @Test
    void programmaticDataSourceSpecValidation() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        DataSource dataSource = DataSource.newDataSource(
                new Metadata("thanos"),
                new DataSourceSpec() {{
                    setDescription("Thanos metrics store");
                    setType("Prometheus");
                    setConnectionDetails(mapper.readTree("[{\"url\":\"http://thanos.example.com\"}]"));
                }});
        assertDoesNotThrow(() -> OpenSloSdk.validate(dataSource));
    }

    @Test
    void sloSpecCompositeFlag() {
        SLO slo = new SLO(new Metadata("composite-slo"), new SLOSpec());
        assertEquals(false, slo.isComposite());
    }
}
