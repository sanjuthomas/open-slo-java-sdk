package com.sanjuthomas.openslo.v1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.sanjuthomas.openslo.validation.Rules;
import com.sanjuthomas.openslo.validation.ValidatorError;

import java.util.List;

public enum SLIRawMetricType {
    SUCCESS("success"),
    FAILURE("failure");

    private final String value;

    SLIRawMetricType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static SLIRawMetricType fromValue(String value) {
        for (SLIRawMetricType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("unsupported SLIRawMetricType: " + value);
    }

    void validate(String path, List<ValidatorError> errors) {
        Rules.oneOf(path, this, List.of(values()), errors);
    }
}
