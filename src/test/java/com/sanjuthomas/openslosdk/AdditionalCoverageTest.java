package com.sanjuthomas.openslosdk;

import com.sanjuthomas.openslo.Kind;
import com.sanjuthomas.openslo.v1.AlertCondition;
import com.sanjuthomas.openslo.v1.AlertNotificationTarget;
import com.sanjuthomas.openslo.v1.DurationShorthand;
import com.sanjuthomas.openslo.v1.Operator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdditionalCoverageTest {

    @Test
    void referenceNotFoundExceptionPrefix() {
        ReferenceNotFoundException ex =
                new ReferenceNotFoundException(Kind.SLI, "spec.indicatorRef", "missing");
        ReferenceNotFoundException prefixed = ex.withFieldPathPrefix("objects[0].");
        assertTrue(prefixed.getFieldPath().startsWith("objects[0]."));
        assertEquals(Kind.SLI, prefixed.getExpectedKind());
    }

    @Test
    void v1DurationShorthandParsing() {
        DurationShorthand duration = DurationShorthand.parse("2w");
        assertEquals(2, duration.getValue());
        assertEquals(DurationShorthand.Unit.WEEK, duration.getUnit());
        assertThrows(IllegalArgumentException.class, () -> DurationShorthand.parse("invalid"));
    }

    @Test
    void v1OperatorFromValue() {
        assertEquals(Operator.GTE, Operator.fromValue("gte"));
    }

    @Test
    void decodeAlertConditionAndNotificationTarget() throws Exception {
        List<com.sanjuthomas.openslo.OpenSloObject> objects = OpenSloSdk.decode(
                TestFixtures.read("test_data/inline/inputs/v1_alert_policies_keep_refs.yaml"),
                ObjectFormat.YAML);
        assertEquals(1, OpenSloSdk.filterByType(objects, AlertCondition.class).size());
        assertEquals(1, OpenSloSdk.filterByType(objects, AlertNotificationTarget.class).size());
    }

    @Test
    void decodeLabelAsScalarValue() throws Exception {
        String yaml = """
            apiVersion: openslo/v1
            kind: Service
            metadata:
              name: scalar-label
              labels:
                env: production
            spec:
              description: Label scalar form
            """;
        OpenSloSdk.validate(OpenSloSdk.decode(yaml, ObjectFormat.YAML));
    }

    @Test
    void decodeJsonSloFixture() throws Exception {
        List<com.sanjuthomas.openslo.OpenSloObject> objects = OpenSloSdk.decode(
                TestFixtures.read("test_data/encode/v1_slo.json"),
                ObjectFormat.JSON);
        assertTrue(objects.size() >= 1);
        OpenSloSdk.validate(objects);
    }

    @Test
    void inlineRemoveReferencedObjectsFromFixture() throws Exception {
        List<com.sanjuthomas.openslo.OpenSloObject> objects = OpenSloSdk.decode(
                TestFixtures.read("test_data/inline/inputs/v1_alert_policies_remove_refs.yaml"),
                ObjectFormat.YAML);
        List<com.sanjuthomas.openslo.OpenSloObject> inlined =
                new ReferenceInliner(objects).removeReferencedObjects().inline();
        assertTrue(inlined.size() < objects.size());
    }

    @Test
    void exportCustomConfigFixture() throws Exception {
        ReferenceConfig config = ReferenceConfig.defaults();
        config.getV1().getAlertPolicy().setAlertCondition(false);

        List<com.sanjuthomas.openslo.OpenSloObject> objects = OpenSloSdk.decode(
                TestFixtures.read("test_data/export/inputs/custom_config.yaml"),
                ObjectFormat.YAML);
        List<com.sanjuthomas.openslo.OpenSloObject> exported =
                new ReferenceExporter(objects).withConfig(config).export();
        assertFalse(exported.isEmpty());
    }
}
