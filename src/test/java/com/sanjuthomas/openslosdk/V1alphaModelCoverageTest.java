package com.sanjuthomas.openslosdk;

import com.sanjuthomas.openslo.OpenSloObject;
import com.sanjuthomas.openslo.v1alpha.Operator;
import com.sanjuthomas.openslo.v1alpha.Service;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class V1alphaModelCoverageTest {

    @Test
    void decodeRichV1alphaSlo() throws Exception {
        String yaml = """
            apiVersion: openslo/v1alpha
            kind: SLO
            metadata:
              name: checkout-latency
              displayName: Checkout latency SLO
            spec:
              service: checkout
              budgetingMethod: Occurrences
              description: Checkout latency objective
              timeWindows:
                - unit: day
                  count: 28
                  isRolling: true
                  calendar:
                    startTime: 2024-01-01 00:00:00
                    timeZone: UTC
              indicator:
                thresholdMetric:
                  source: prometheus
                  queryType: promql
                  query: histogram_quantile(0.99, rate(http_request_duration_seconds_bucket[5m]))
              objectives:
                - displayName: P99 latency
                  target: 0.99
                  op: lt
                  ratioMetrics:
                    incremental: true
                    good:
                      source: prometheus
                      queryType: promql
                      query: histogram_quantile(0.99, rate(http_request_duration_seconds_bucket{status_code=\"200\"}[5m]))
                    total:
                      source: prometheus
                      queryType: promql
                      query: histogram_quantile(0.99, rate(http_request_duration_seconds_bucket[5m]))
            """;
        List<OpenSloObject> objects = OpenSloSdk.decode(yaml, ObjectFormat.YAML);
        assertEquals(1, objects.size());
        assertDoesNotThrow(() -> OpenSloSdk.validate(objects));
    }

    @Test
    void v1alphaOperatorEnumValues() {
        assertEquals(Operator.LT, Operator.fromValue("lt"));
        assertEquals(Operator.GT, Operator.fromValue("gt"));
    }

    @Test
    void decodeV1alphaServiceSpec() throws Exception {
        String yaml = """
            apiVersion: openslo/v1alpha
            kind: Service
            metadata:
              name: billing
            spec:
              description: Billing service
            """;
        List<OpenSloObject> objects = OpenSloSdk.decode(yaml, ObjectFormat.YAML);
        Service service = (Service) objects.get(0);
        assertEquals("Billing service", service.getSpec().getDescription());
        assertDoesNotThrow(() -> OpenSloSdk.validate(service));
    }
}
