package com.sanjuthomas.openslosdk;

import com.sanjuthomas.openslo.OpenSloObject;
import com.sanjuthomas.openslo.v1.AlertCondition;
import com.sanjuthomas.openslo.v1.AlertNotificationTarget;
import com.sanjuthomas.openslo.v1.AlertPolicy;
import com.sanjuthomas.openslo.v1.DataSource;
import com.sanjuthomas.openslo.v1.Metadata;
import com.sanjuthomas.openslo.v1.SLI;
import com.sanjuthomas.openslo.v1.SLO;
import com.sanjuthomas.openslo.v1.Service;
import com.sanjuthomas.openslo.validation.ValidationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidationTest {

    @Test
    void validateV1ServiceFromFixture() throws IOException {
        List<OpenSloObject> objects = OpenSloSdk.decode(
                TestFixtures.read("test_data/decode/single_object.yaml"),
                ObjectFormat.YAML);
        assertDoesNotThrow(() -> OpenSloSdk.validate(objects));
    }

    @Test
    void validateV1SloFromFixture() throws IOException {
        List<OpenSloObject> objects = OpenSloSdk.decode(
                TestFixtures.read("test_data/decode/v1_slos.yaml"),
                ObjectFormat.YAML);
        assertDoesNotThrow(() -> OpenSloSdk.validate(objects));
    }

    @Test
    void validateV2alphaObjectsFromFixture() throws IOException {
        List<OpenSloObject> objects = OpenSloSdk.decode(
                TestFixtures.read("test_data/decode/v2alpha_slos.yaml"),
                ObjectFormat.YAML);
        assertDoesNotThrow(() -> OpenSloSdk.validate(objects));
    }

    @Test
    void validateV2alphaDataSourceFromFixture() throws IOException {
        List<OpenSloObject> objects = OpenSloSdk.decode(
                TestFixtures.read("test_data/decode/v2alpha_data_source.yaml"),
                ObjectFormat.YAML);
        assertEquals(1, objects.size());
        assertThrows(ValidationException.class, () -> OpenSloSdk.validate(objects));
    }

    @Test
    void validateInlinedAlertPolicies() throws Exception {
        List<OpenSloObject> objects = OpenSloSdk.decode(
                TestFixtures.read("test_data/inline/inputs/v1_alert_policies_keep_refs.yaml"),
                ObjectFormat.YAML);
        List<OpenSloObject> inlined = new ReferenceInliner(objects).inline();
        assertDoesNotThrow(() -> OpenSloSdk.validate(inlined));
    }

    @Test
    void validateExportedSlo() throws IOException {
        List<OpenSloObject> objects = OpenSloSdk.decode(
                TestFixtures.read("test_data/export/inputs/v1_slo.yaml"),
                ObjectFormat.YAML);
        List<OpenSloObject> exported = new ReferenceExporter(objects).export();
        assertDoesNotThrow(() -> OpenSloSdk.validate(exported));
    }

    @Test
    void validateRejectsServiceWithoutMetadata() {
        Service service = new Service();
        assertThrows(ValidationException.class, () -> OpenSloSdk.validate(service));
    }

    @Test
    void validateRejectsSloWithoutRequiredFields() {
        SLO slo = new SLO();
        assertThrows(ValidationException.class, () -> OpenSloSdk.validate(slo));
    }

    @Test
    void validateRejectsSliWithoutRequiredFields() {
        SLI sli = new SLI();
        assertThrows(ValidationException.class, () -> OpenSloSdk.validate(sli));
    }

    @Test
    void validateRejectsDataSourceWithoutRequiredFields() {
        DataSource dataSource = new DataSource();
        assertThrows(ValidationException.class, () -> OpenSloSdk.validate(dataSource));
    }

    @Test
    void validateRejectsAlertPolicyWithoutRequiredFields() {
        AlertPolicy policy = new AlertPolicy();
        assertThrows(ValidationException.class, () -> OpenSloSdk.validate(policy));
    }

    @Test
    void validateRejectsAlertConditionWithoutRequiredFields() {
        AlertCondition condition = new AlertCondition();
        assertThrows(ValidationException.class, () -> OpenSloSdk.validate(condition));
    }

    @Test
    void validateRejectsAlertNotificationTargetWithoutRequiredFields() {
        AlertNotificationTarget target = new AlertNotificationTarget();
        assertThrows(ValidationException.class, () -> OpenSloSdk.validate(target));
    }

    @Test
    void validateRejectsInvalidMetadataName() {
        Service service = new Service();
        service.setMetadata(new Metadata("INVALID NAME"));
        assertThrows(ValidationException.class, () -> OpenSloSdk.validate(service));
    }
}
