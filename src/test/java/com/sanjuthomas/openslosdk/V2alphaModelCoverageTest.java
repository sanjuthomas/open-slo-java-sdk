package com.sanjuthomas.openslosdk;

import com.sanjuthomas.openslo.OpenSloObject;
import com.sanjuthomas.openslo.v2alpha.DataSource;
import com.sanjuthomas.openslo.v2alpha.DurationShorthand;
import com.sanjuthomas.openslo.v2alpha.Operator;
import com.sanjuthomas.openslo.v2alpha.SLO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class V2alphaModelCoverageTest {

    @Test
    void decodeComprehensiveV2alphaDocument() throws Exception {
        String yaml = """
            - apiVersion: openslo.com/v2alpha
              kind: DataSource
              metadata:
                name: prometheus
              spec:
                description: Prometheus metrics
                type: prometheus
                connectionDetails:
                  url: http://prometheus.example.com
            - apiVersion: openslo.com/v2alpha
              kind: SLO
              metadata:
                name: checkout-availability
                labels:
                  tier: gold
              spec:
                description: Checkout availability objective
                serviceRef: checkout
                budgetingMethod: Occurrences
                timeWindow:
                  - duration: 30d
                    isRolling: true
                sli:
                  metadata:
                    name: checkout-errors
                  spec:
                    ratioMetric:
                      counter: true
                      good:
                        dataSourceRef: prometheus
                        spec:
                          query: sum(rate(http_requests_total{status=~\"2..\"}[5m]))
                      total:
                        dataSourceRef: prometheus
                        spec:
                          query: sum(rate(http_requests_total[5m]))
                objectives:
                  - displayName: Availability
                    target: 0.999
                    op: gte
                    timeSliceTarget: 0.99
                    timeSliceWindow: 5m
                alertPolicies:
                  - alertPolicyRef: checkout-policy
            - apiVersion: openslo.com/v2alpha
              kind: AlertPolicy
              metadata:
                name: checkout-policy
              spec:
                description: Checkout alerts
            """;
        List<OpenSloObject> objects = OpenSloSdk.decode(yaml, ObjectFormat.YAML);
        assertEquals(3, objects.size());
        assertDoesNotThrow(() -> OpenSloSdk.validate(objects));

        SLO slo = (SLO) objects.get(1);
        assertNotNull(slo.getSpec().getTimeWindow());
        assertNotNull(slo.getSpec().getObjectives());
        DataSource dataSource = (DataSource) objects.get(0);
        assertEquals("prometheus", dataSource.getName());
    }

    @Test
    void v2alphaDurationShorthandAndOperator() {
        List<com.sanjuthomas.openslo.validation.ValidatorError> errors = new java.util.ArrayList<>();
        DurationShorthand duration = DurationShorthand.parse("1h");
        duration.validate("timeSliceWindow", errors);
        assertEquals(Operator.GTE, Operator.fromValue("gte"));
        assertEquals(0, errors.size());
    }

    @Test
    void decodeExistingV2alphaFixtures() throws Exception {
        List<OpenSloObject> slos =
                OpenSloSdk.decode(TestFixtures.read("test_data/decode/v2alpha_slos.yaml"), ObjectFormat.YAML);
        List<OpenSloObject> dataSources = OpenSloSdk.decode(
                TestFixtures.read("test_data/decode/v2alpha_data_source.yaml"), ObjectFormat.YAML);
        assertDoesNotThrow(() -> OpenSloSdk.validate(slos));
        assertEquals(1, dataSources.size());
    }
}
