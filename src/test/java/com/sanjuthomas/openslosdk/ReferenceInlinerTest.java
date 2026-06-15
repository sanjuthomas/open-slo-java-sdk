package com.sanjuthomas.openslosdk;

import com.sanjuthomas.openslo.OpenSloObject;
import com.sanjuthomas.openslo.v1.AlertPolicy;
import com.sanjuthomas.openslo.v1.SLO;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReferenceInlinerTest {

    @Test
    void inlineV1SloReferences() throws Exception {
        List<OpenSloObject> objects =
                OpenSloSdk.decode(TestFixtures.read("test_data/inline/inputs/v1_slo.yaml"), ObjectFormat.YAML);
        List<OpenSloObject> inlined = new ReferenceInliner(objects).inline();

        List<SLO> slos = OpenSloSdk.filterByType(inlined, SLO.class);
        assertEquals(2, slos.size());
        assertTrue(slos.stream().allMatch(slo -> slo.getSpec().getIndicator() != null));
    }

    @Test
    void inlineV1AlertPolicies() throws Exception {
        List<OpenSloObject> objects = OpenSloSdk.decode(
                TestFixtures.read("test_data/inline/inputs/v1_alert_policies_keep_refs.yaml"),
                ObjectFormat.YAML);
        List<OpenSloObject> inlined = new ReferenceInliner(objects).inline();

        List<AlertPolicy> policies = OpenSloSdk.filterByType(inlined, AlertPolicy.class);
        assertEquals(1, policies.size());
    }

    @Test
    void inlineRemovesReferencedObjectsWhenConfigured() throws Exception {
        List<OpenSloObject> objects = OpenSloSdk.decode(
                TestFixtures.read("test_data/inline/inputs/v1_slo.yaml"),
                ObjectFormat.YAML);
        int originalSize = objects.size();

        List<OpenSloObject> inlined =
                new ReferenceInliner(objects).removeReferencedObjects().inline();

        assertTrue(inlined.size() < originalSize);
    }

    @Test
    void inlineWithCustomConfig() throws Exception {
        ReferenceConfig config = ReferenceConfig.defaults();
        config.getV1().getSlo().setSli(false);
        config.getV1().getSlo().setAlertPolicy(false);

        List<OpenSloObject> objects = OpenSloSdk.decode(
                TestFixtures.read("test_data/inline/inputs/v1_slo.yaml"),
                ObjectFormat.YAML);
        List<OpenSloObject> inlined = new ReferenceInliner(objects).withConfig(config).inline();

        List<SLO> slos = OpenSloSdk.filterByType(inlined, SLO.class);
        assertTrue(slos.stream().anyMatch(slo -> "my-sli".equals(slo.getSpec().getIndicatorRef())));
    }

    @Test
    void inlineThrowsWhenSliReferenceMissing() throws IOException {
        List<OpenSloObject> objects = OpenSloSdk.decode(
                TestFixtures.read("test_data/inline/inputs/v1_slo_invalid_sli.yaml"),
                ObjectFormat.YAML);

        ReferenceNotFoundException ex = assertThrows(
                ReferenceNotFoundException.class,
                () -> new ReferenceInliner(objects).inline());

        assertEquals("no-sli", ex.getObjectName());
        assertEquals("spec.indicatorRef", ex.getFieldPath());
    }

    @Test
    void inlineThrowsWhenAlertPolicyReferenceMissing() throws IOException {
        List<OpenSloObject> objects = OpenSloSdk.decode(
                TestFixtures.read("test_data/inline/inputs/v1_slo_invalid_policy.yaml"),
                ObjectFormat.YAML);

        ReferenceNotFoundException ex = assertThrows(
                ReferenceNotFoundException.class,
                () -> new ReferenceInliner(objects).inline());

        assertTrue(ex.getFieldPath().contains("alertPolicies"));
    }

    @Test
    void inlineThrowsWhenNotificationTargetMissing() throws IOException {
        List<OpenSloObject> objects = OpenSloSdk.decode(
                TestFixtures.read("test_data/inline/inputs/v1_alert_policies_invalid_target.yaml"),
                ObjectFormat.YAML);

        ReferenceNotFoundException ex = assertThrows(
                ReferenceNotFoundException.class,
                () -> new ReferenceInliner(objects).inline());

        assertTrue(ex.getFieldPath().contains("notificationTargets"));
    }

    @Test
    void inlineThrowsWhenConditionReferenceMissing() throws IOException {
        List<OpenSloObject> objects = OpenSloSdk.decode(
                TestFixtures.read("test_data/inline/inputs/v1_alert_policies_invalid_condition.yaml"),
                ObjectFormat.YAML);

        ReferenceNotFoundException ex = assertThrows(
                ReferenceNotFoundException.class,
                () -> new ReferenceInliner(objects).inline());

        assertTrue(ex.getFieldPath().contains("conditions"));
    }

    @Test
    void inlineIsIdempotent() throws Exception {
        ReferenceInliner inliner = new ReferenceInliner(
                OpenSloSdk.decode(TestFixtures.read("test_data/inline/inputs/v1_slo.yaml"), ObjectFormat.YAML));

        List<OpenSloObject> first = inliner.inline();
        List<OpenSloObject> second = inliner.inline();

        assertEquals(first.size(), second.size());
    }

    @Test
    void inlineAcceptsVarargsConstructor() throws Exception {
        List<OpenSloObject> objects =
                OpenSloSdk.decode(TestFixtures.read("test_data/decode/single_object.yaml"), ObjectFormat.YAML);
        List<OpenSloObject> inlined = new ReferenceInliner(objects.get(0)).inline();
        assertEquals(1, inlined.size());
    }
}
