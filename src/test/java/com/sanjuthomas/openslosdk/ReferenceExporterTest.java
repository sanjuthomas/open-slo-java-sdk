package com.sanjuthomas.openslosdk;

import com.sanjuthomas.openslo.Kind;
import com.sanjuthomas.openslo.OpenSloObject;
import com.sanjuthomas.openslo.v1.AlertCondition;
import com.sanjuthomas.openslo.v1.AlertNotificationTarget;
import com.sanjuthomas.openslo.v1.AlertPolicy;
import com.sanjuthomas.openslo.v1.SLI;
import com.sanjuthomas.openslo.v1.SLO;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReferenceExporterTest {

    @Test
    void exportV1SloExtractsSli() throws IOException {
        List<OpenSloObject> objects = OpenSloSdk.decode(
                TestFixtures.read("test_data/export/inputs/v1_slo.yaml"),
                ObjectFormat.YAML);

        List<OpenSloObject> exported = new ReferenceExporter(objects).export();

        SLO slo = (SLO) exported.get(0);
        assertNotNull(slo.getSpec().getIndicatorRef());
        assertNull(slo.getSpec().getIndicator());
        assertTrue(exported.stream().anyMatch(o -> o.getKind() == Kind.SLI));
    }

    @Test
    void exportV1AlertPoliciesExtractsReferences() throws IOException {
        List<OpenSloObject> objects = OpenSloSdk.decode(
                TestFixtures.read("test_data/export/inputs/v1_alert_policies.yaml"),
                ObjectFormat.YAML);

        List<OpenSloObject> exported = new ReferenceExporter(objects).export();

        assertTrue(exported.size() > objects.size());
        assertTrue(exported.stream().anyMatch(o -> o instanceof AlertCondition));
        assertTrue(exported.stream().anyMatch(o -> o instanceof AlertNotificationTarget));
    }

    @Test
    void exportWithCustomConfigSkipsSli() throws IOException {
        ReferenceConfig config = ReferenceConfig.defaults();
        config.getV1().getSlo().setSli(false);

        List<OpenSloObject> objects = OpenSloSdk.decode(
                TestFixtures.read("test_data/export/inputs/custom_config.yaml"),
                ObjectFormat.YAML);

        List<OpenSloObject> exported = new ReferenceExporter(objects).withConfig(config).export();

        SLO slo = (SLO) exported.get(0);
        assertNotNull(slo.getSpec().getIndicator());
        assertTrue(exported.stream().noneMatch(o -> o instanceof SLI));
    }

    @Test
    void exportIsIdempotent() throws IOException {
        ReferenceExporter exporter = new ReferenceExporter(
                OpenSloSdk.decode(TestFixtures.read("test_data/export/inputs/v1_slo.yaml"), ObjectFormat.YAML));

        List<OpenSloObject> first = exporter.export();
        List<OpenSloObject> second = exporter.export();

        assertEquals(first.size(), second.size());
    }

    @Test
    void exportAcceptsVarargsConstructor() throws IOException {
        List<OpenSloObject> objects = OpenSloSdk.decode(
                TestFixtures.read("test_data/decode/single_object.yaml"),
                ObjectFormat.YAML);
        List<OpenSloObject> exported = new ReferenceExporter(objects.get(0)).export();
        assertEquals(1, exported.size());
    }

    @Test
    void exportV1AlertPolicyWithInlineTargetsAndConditions() throws IOException {
        List<OpenSloObject> objects = OpenSloSdk.decode(
                TestFixtures.read("test_data/export/inputs/v1_alert_policies.yaml"),
                ObjectFormat.YAML);

        List<OpenSloObject> exported = new ReferenceExporter(objects).export();
        List<AlertPolicy> policies = OpenSloSdk.filterByType(exported, AlertPolicy.class);

        AlertPolicy policy = policies.get(0);
        assertTrue(policy.getSpec().getConditions().stream().allMatch(c -> c.isRef()));
        assertTrue(policy.getSpec().getNotificationTargets().stream().allMatch(t -> t.isRef()));
    }
}
