package com.sanjuthomas.openslo.validation;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class Rules {
    private static final Pattern DNS_LABEL = Pattern.compile("^[a-z0-9]([-a-z0-9]*[a-z0-9])?$");
    private static final Pattern LABEL_KEY = Pattern.compile("^[a-zA-Z0-9]([-._a-zA-Z0-9]{0,61}[a-zA-Z0-9])?$");
    private static final Pattern ANNOTATION_KEY_LENGTH =
            Pattern.compile("^(.{0,253}/)?.{0,63}$");
    private static final Pattern ANNOTATION_KEY =
            Pattern.compile("^([a-z0-9]([-a-z0-9]{0,61}[a-z0-9])?(\\.[a-z0-9]([-a-z0-9]{0,61}[a-z0-9])?)*/)?[a-zA-Z0-9]([-._a-zA-Z0-9]{0,61}[a-zA-Z0-9])?$");
    private static final Pattern K8S_QUALIFIED_NAME =
            Pattern.compile("^([a-z0-9]([-a-z0-9]{0,61}[a-z0-9])?(\\.[a-z0-9]([-a-z0-9]{0,61}[a-z0-9])?)*/)?[a-zA-Z0-9]([-._a-zA-Z0-9]{0,61}[a-zA-Z0-9])?$");
    private static final Pattern LABEL_VALUE_V2 =
            Pattern.compile("^([a-z0-9]([-._a-z0-9]{0,61}[a-z0-9])?)?$");
    private static final Pattern ALPHABETIC = Pattern.compile("^[a-zA-Z]+$");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Rules() {}

    public static void required(String name, Object value, List<ValidatorError> errors) {
        if (value == null || (value instanceof String s && s.isEmpty())) {
            errors.add(new ValidatorError("'" + name + "' is required").withPropertyPath(name));
        }
    }

    public static void maxLength(String name, String value, int max, List<ValidatorError> errors) {
        if (value != null && value.length() > max) {
            errors.add(new ValidatorError("'" + name + "' must be at most " + max + " characters")
                    .withPropertyPath(name));
        }
    }

    public static void dnsLabel(String name, String value, List<ValidatorError> errors) {
        if (value != null && !value.isEmpty() && !DNS_LABEL.matcher(value).matches()) {
            errors.add(new ValidatorError("'" + name + "' must be a valid DNS label")
                    .withPropertyPath(name));
        }
    }

    public static void oneOf(String name, Object value, Collection<?> allowed, List<ValidatorError> errors) {
        if (value != null && allowed.stream().noneMatch(v -> Objects.equals(v, value))) {
            errors.add(new ValidatorError("'" + name + "' has unsupported value: " + value)
                    .withPropertyPath(name));
        }
    }

    public static void eq(String name, Object value, Object expected, List<ValidatorError> errors) {
        if (value != null && !Objects.equals(value, expected)) {
            errors.add(new ValidatorError("'" + name + "' must equal '" + expected + "'")
                    .withPropertyPath(name));
        }
    }

    public static void gte(String name, Number value, double min, List<ValidatorError> errors) {
        if (value != null && value.doubleValue() < min) {
            errors.add(new ValidatorError("'" + name + "' must be >= " + min).withPropertyPath(name));
        }
    }

    public static void gt(String name, Number value, double min, List<ValidatorError> errors) {
        if (value != null && value.doubleValue() <= min) {
            errors.add(new ValidatorError("'" + name + "' must be > " + min).withPropertyPath(name));
        }
    }

    public static void lt(String name, Number value, double max, List<ValidatorError> errors) {
        if (value != null && value.doubleValue() >= max) {
            errors.add(new ValidatorError("'" + name + "' must be < " + max).withPropertyPath(name));
        }
    }

    public static void lte(String name, Number value, double max, List<ValidatorError> errors) {
        if (value != null && value.doubleValue() > max) {
            errors.add(new ValidatorError("'" + name + "' must be <= " + max).withPropertyPath(name));
        }
    }

    public static void forbidden(String name, Object value, List<ValidatorError> errors) {
        if (value != null && (!(value instanceof String s) || !s.isEmpty())) {
            errors.add(new ValidatorError("'" + name + "' is forbidden").withPropertyPath(name));
        }
    }

    public static void sliceLength(String name, List<?> slice, int min, int max, List<ValidatorError> errors) {
        if (slice == null) {
            return;
        }
        if (slice.size() < min || slice.size() > max) {
            errors.add(new ValidatorError("'" + name + "' must have between " + min + " and " + max + " elements")
                    .withPropertyPath(name));
        }
    }

    public static void sliceMinLength(String name, List<?> slice, int min, List<ValidatorError> errors) {
        if (slice != null && slice.size() < min) {
            errors.add(new ValidatorError("'" + name + "' must have at least " + min + " elements")
                    .withPropertyPath(name));
        }
    }

    public static void mapMinLength(String name, Map<?, ?> map, int min, List<ValidatorError> errors) {
        if (map != null && map.size() < min) {
            errors.add(new ValidatorError("'" + name + "' must have at least " + min + " entries")
                    .withPropertyPath(name));
        }
    }

    public static void mutuallyExclusive(
            String path,
            boolean exclusive,
            Map<String, Object> fields,
            List<ValidatorError> errors) {
        long setCount = fields.values().stream()
                .filter(Rules::isSet)
                .count();
        if (exclusive && setCount > 1) {
            errors.add(new ValidatorError("fields " + fields.keySet() + " are mutually exclusive at '" + path + "'")
                    .withPropertyPath(path));
        } else if (!exclusive && setCount > 1) {
            errors.add(new ValidatorError("fields " + fields.keySet() + " cannot be set together at '" + path + "'")
                    .withPropertyPath(path));
        }
    }

    public static void oneOfProperties(String path, Map<String, Object> fields, List<ValidatorError> errors) {
        long setCount = fields.values().stream().filter(Rules::isSet).count();
        if (setCount == 0) {
            errors.add(new ValidatorError("one of " + fields.keySet() + " must be set at '" + path + "'")
                    .withPropertyPath(path));
        }
    }

    public static void labelKeys(String path, Map<String, ?> labels, List<ValidatorError> errors) {
        if (labels == null) {
            return;
        }
        for (String key : labels.keySet()) {
            if (!LABEL_KEY.matcher(key).matches()) {
                errors.add(new ValidatorError("invalid label key '" + key + "'").withPropertyPath(path + "." + key));
            }
        }
    }

    public static void annotationKeys(String path, Map<String, String> annotations, List<ValidatorError> errors) {
        if (annotations == null) {
            return;
        }
        for (String key : annotations.keySet()) {
            if (!ANNOTATION_KEY_LENGTH.matcher(key).matches() || !ANNOTATION_KEY.matcher(key).matches()) {
                errors.add(new ValidatorError("invalid annotation key '" + key + "'")
                        .withPropertyPath(path + "." + key));
            }
        }
    }

    public static void k8sQualifiedNameKeys(String path, Map<String, ?> map, List<ValidatorError> errors) {
        if (map == null) {
            return;
        }
        for (String key : map.keySet()) {
            if (!K8S_QUALIFIED_NAME.matcher(key).matches()) {
                errors.add(new ValidatorError("invalid key '" + key + "'").withPropertyPath(path + "." + key));
            }
        }
    }

    public static void labelValueV2(String path, String value, List<ValidatorError> errors) {
        if (value != null && !LABEL_VALUE_V2.matcher(value).matches()) {
            errors.add(new ValidatorError("invalid label value at '" + path + "'").withPropertyPath(path));
        }
    }

    public static void dateTime(String name, String value, List<ValidatorError> errors) {
        if (value == null || value.isEmpty()) {
            return;
        }
        try {
            java.time.LocalDateTime.parse(value, DATE_TIME);
        } catch (DateTimeParseException ex) {
            errors.add(new ValidatorError("'" + name + "' must be a valid datetime").withPropertyPath(name));
        }
    }

    public static void timeZone(String name, String value, List<ValidatorError> errors) {
        if (value == null || value.isEmpty()) {
            return;
        }
        try {
            ZoneId.of(value);
        } catch (Exception ex) {
            errors.add(new ValidatorError("'" + name + "' must be a valid timezone").withPropertyPath(name));
        }
    }

    public static void alphabetic(String name, String value, List<ValidatorError> errors) {
        if (value != null && !value.isEmpty() && !ALPHABETIC.matcher(value).matches()) {
            errors.add(new ValidatorError("'" + name + "' must be alphabetic").withPropertyPath(name));
        }
    }

    public static void nonEmpty(String name, String value, List<ValidatorError> errors) {
        if (value != null && value.isEmpty()) {
            errors.add(new ValidatorError("'" + name + "' must not be empty").withPropertyPath(name));
        }
    }

    public static Map<String, Object> fieldsMap(Object... keyValues) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            map.put((String) keyValues[i], keyValues[i + 1]);
        }
        return map;
    }

    public static boolean isSet(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof String s) {
            return !s.isEmpty();
        }
        if (value instanceof Collection<?> c) {
            return !c.isEmpty();
        }
        if (value instanceof Map<?, ?> m) {
            return !m.isEmpty();
        }
        return true;
    }

    public static void throwIfErrors(List<ValidatorError> errors) throws ValidationException {
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    public static void validateMetadata(
            String path,
            String name,
            String displayName,
            Map<String, ?> labels,
            Map<String, String> annotations,
            boolean includeDisplayName,
            boolean includeLabels,
            boolean includeAnnotations,
            List<ValidatorError> errors) {
        Rules.required(path + ".name", name, errors);
        Rules.dnsLabel(path + ".name", name, errors);
        if (includeDisplayName) {
            Rules.maxLength(path + ".displayName", displayName, 63, errors);
        }
        if (includeLabels) {
            Rules.labelKeys(path + ".labels", labels, errors);
        }
        if (includeAnnotations) {
            Rules.annotationKeys(path + ".annotations", annotations, errors);
        }
    }
}
