package com.sanjuthomas.openslosdk;

import com.sanjuthomas.openslo.OpenSloObject;
import com.sanjuthomas.openslo.v1.SLI;
import com.sanjuthomas.openslo.v1.SLO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FixtureCoverageTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "test_data/inline/inputs/v1_slo.yaml",
            "test_data/inline/inputs/v1_alert_policies_remove_refs.yaml",
            "test_data/inline/inputs/v1_slo_invalid_target.yaml",
            "test_data/export/inputs/v1_alert_policies.yaml",
            "test_data/export/inputs/custom_config.yaml"
    })
    void decodeAndValidateV1Fixtures(String fixture) throws Exception {
        List<OpenSloObject> objects = OpenSloSdk.decode(TestFixtures.read(fixture), ObjectFormat.YAML);
        assertFalse(objects.isEmpty());
        assertDoesNotThrow(() -> OpenSloSdk.validate(objects));
    }

    @Test
    void decodeV1SloFixtureContainsCoreTypes() throws IOException {
        List<OpenSloObject> objects =
                OpenSloSdk.decode(TestFixtures.read("test_data/inline/inputs/v1_slo.yaml"), ObjectFormat.YAML);

        assertFalse(OpenSloSdk.filterByType(objects, SLO.class).isEmpty());
        assertFalse(OpenSloSdk.filterByType(objects, SLI.class).isEmpty());
    }

    @Test
    void decodeV1alphaServiceAndSlo() throws Exception {
        String yaml = """
            - apiVersion: openslo/v1alpha
              kind: Service
              metadata:
                name: checkout
                displayName: Checkout Service
              spec:
                description: Handles checkout
            - apiVersion: openslo/v1alpha
              kind: SLO
              metadata:
                name: checkout-latency
              spec:
                service: checkout
                budgetingMethod: Occurrences
                indicator:
                  thresholdMetric:
                    source: prometheus
                    queryType: promql
                    query: histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m]))
                objectives:
                  - target: 0.99
            """;
        List<OpenSloObject> objects = OpenSloSdk.decode(yaml, ObjectFormat.YAML);
        assertEquals(2, objects.size());
        assertDoesNotThrow(() -> OpenSloSdk.validate(objects));
    }

    @Test
    void decodeAndValidateV2alphaSlos() throws Exception {
        List<OpenSloObject> objects =
                OpenSloSdk.decode(TestFixtures.read("test_data/decode/v2alpha_slos.yaml"), ObjectFormat.YAML);
        assertTrue(objects.size() >= 2);
        assertDoesNotThrow(() -> OpenSloSdk.validate(objects));
    }

    @Test
    void decodeV2alphaServiceSliAndAlertTypes() throws Exception {
        String yaml = """
            - apiVersion: openslo.com/v2alpha
              kind: Service
              metadata:
                name: payments
              spec:
                description: Payment processing
            - apiVersion: openslo.com/v2alpha
              kind: SLI
              metadata:
                name: payments-latency
              spec:
                thresholdMetric:
                  dataSourceRef: prometheus
                  spec:
                    query: histogram_quantile(0.99, sum(rate(http_request_duration_seconds_bucket[5m])) by (le))
            - apiVersion: openslo.com/v2alpha
              kind: AlertPolicy
              metadata:
                name: payments-policy
              spec:
                description: Payments alert policy
            - apiVersion: openslo.com/v2alpha
              kind: AlertCondition
              metadata:
                name: payments-condition
              spec:
                description: Burn rate alert
            - apiVersion: openslo.com/v2alpha
              kind: AlertNotificationTarget
              metadata:
                name: payments-target
              spec:
                description: PagerDuty target
            """;
        List<OpenSloObject> objects = OpenSloSdk.decode(yaml, ObjectFormat.YAML);
        assertEquals(5, objects.size());
        assertDoesNotThrow(() -> OpenSloSdk.validate(objects));
    }

    @Test
    void inlineAndExportLargeV1Fixture() throws Exception {
        List<OpenSloObject> objects =
                OpenSloSdk.decode(TestFixtures.read("test_data/inline/inputs/v1_slo.yaml"), ObjectFormat.YAML);

        List<OpenSloObject> inlined = new ReferenceInliner(objects).inline();
        List<OpenSloObject> exported = new ReferenceExporter(inlined).export();

        assertNotNull(inlined);
        assertFalse(exported.isEmpty());
        assertDoesNotThrow(() -> OpenSloSdk.validate(exported));
    }
}
