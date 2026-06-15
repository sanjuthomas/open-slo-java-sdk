package com.sanjuthomas.openslo.validation;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RulesTest {

    @Test
    void requiredRejectsNullAndEmpty() {
        List<ValidatorError> errors = new ArrayList<>();
        Rules.required("name", null, errors);
        Rules.required("name", "", errors);
        assertEquals(2, errors.size());
    }

    @Test
    void maxLengthRejectsLongValues() {
        List<ValidatorError> errors = new ArrayList<>();
        Rules.maxLength("name", "abcdefghij", 5, errors);
        assertEquals(1, errors.size());
    }

    @Test
    void dnsLabelValidatesFormat() {
        List<ValidatorError> errors = new ArrayList<>();
        Rules.dnsLabel("name", "valid-name", errors);
        Rules.dnsLabel("name", "Invalid_Name", errors);
        assertEquals(1, errors.size());
    }

    @Test
    void oneOfRejectsUnsupportedValues() {
        List<ValidatorError> errors = new ArrayList<>();
        Rules.oneOf("kind", "Unknown", List.of("SLO", "SLI"), errors);
        assertEquals(1, errors.size());
    }

    @Test
    void eqRejectsMismatch() {
        List<ValidatorError> errors = new ArrayList<>();
        Rules.eq("apiVersion", "openslo/v2", "openslo/v1", errors);
        assertEquals(1, errors.size());
    }

    @Test
    void numericComparisons() {
        List<ValidatorError> errors = new ArrayList<>();
        Rules.gte("target", 0.5, 0.9, errors);
        Rules.gt("target", 0.9, 0.9, errors);
        Rules.lt("target", 1.0, 0.9, errors);
        Rules.lte("target", 1.1, 1.0, errors);
        assertEquals(4, errors.size());
    }

    @Test
    void forbiddenRejectsSetValues() {
        List<ValidatorError> errors = new ArrayList<>();
        Rules.forbidden("indicatorRef", "my-sli", errors);
        assertEquals(1, errors.size());
    }

    @Test
    void sliceAndMapLengthRules() {
        List<ValidatorError> errors = new ArrayList<>();
        Rules.sliceLength("items", List.of(), 1, 3, errors);
        Rules.sliceMinLength("items", List.of(), 2, errors);
        Rules.mapMinLength("labels", Map.of(), 1, errors);
        assertEquals(3, errors.size());
    }

    @Test
    void mutuallyExclusiveAndOneOfProperties() {
        List<ValidatorError> errors = new ArrayList<>();
        Rules.mutuallyExclusive(
                "spec",
                true,
                Rules.fieldsMap("indicator", "inline", "indicatorRef", "ref"),
                errors);
        Rules.oneOfProperties("spec", Rules.fieldsMap("indicator", null, "indicatorRef", null), errors);
        assertEquals(2, errors.size());
    }

    @Test
    void labelAndAnnotationKeys() {
        List<ValidatorError> errors = new ArrayList<>();
        Rules.labelKeys("metadata.labels", Map.of("valid-key", "v"), errors);
        Rules.labelKeys("metadata.labels", Map.of("invalid key!", "v"), errors);
        Rules.annotationKeys("metadata.annotations", Map.of("valid/annotation", "v"), errors);
        Rules.annotationKeys("metadata.annotations", Map.of("INVALID!", "v"), errors);
        assertEquals(2, errors.size());
    }

    @Test
    void k8sQualifiedNameAndLabelValueV2() {
        List<ValidatorError> errors = new ArrayList<>();
        Rules.k8sQualifiedNameKeys("spec", Map.of("team.example/app", "v"), errors);
        Rules.k8sQualifiedNameKeys("spec", Map.of("INVALID!", "v"), errors);
        Rules.labelValueV2("labels.env", "prod", errors);
        Rules.labelValueV2("labels.env", "INVALID!", errors);
        assertEquals(2, errors.size());
    }

    @Test
    void dateTimeAndTimeZone() {
        List<ValidatorError> errors = new ArrayList<>();
        Rules.dateTime("timestamp", "2024-01-15 10:30:00", errors);
        Rules.dateTime("timestamp", "not-a-date", errors);
        Rules.timeZone("timezone", "UTC", errors);
        Rules.timeZone("timezone", "Not/A/Zone", errors);
        assertEquals(2, errors.size());
    }

    @Test
    void alphabeticAndNonEmpty() {
        List<ValidatorError> errors = new ArrayList<>();
        Rules.alphabetic("severity", "page", errors);
        Rules.alphabetic("severity", "page1", errors);
        Rules.nonEmpty("name", "", errors);
        assertEquals(2, errors.size());
    }

    @Test
    void isSetDetectsEmptyCollections() {
        assertFalse(Rules.isSet(null));
        assertFalse(Rules.isSet(""));
        assertFalse(Rules.isSet(List.of()));
        assertFalse(Rules.isSet(Map.of()));
        assertTrue(Rules.isSet("value"));
        assertTrue(Rules.isSet(List.of("a")));
    }

    @Test
    void throwIfErrorsThrowsValidationException() {
        List<ValidatorError> errors = List.of(new ValidatorError("invalid"));
        assertThrows(ValidationException.class, () -> Rules.throwIfErrors(errors));
    }

    @Test
    void validateMetadataChecksRequiredFields() {
        List<ValidatorError> errors = new ArrayList<>();
        Rules.validateMetadata(
                "metadata",
                null,
                null,
                null,
                null,
                true,
                true,
                true,
                errors);
        assertFalse(errors.isEmpty());
    }
}
