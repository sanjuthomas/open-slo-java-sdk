package com.sanjuthomas.openslo.v1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.sanjuthomas.openslo.validation.Rules;
import com.sanjuthomas.openslo.validation.ValidatorError;

import java.util.List;

public enum AlertConditionKind {
    BURN_RATE("burnrate");

    private final String value;

    AlertConditionKind(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static AlertConditionKind fromValue(String value) {
        for (AlertConditionKind kind : values()) {
            if (kind.value.equals(value)) {
                return kind;
            }
        }
        throw new IllegalArgumentException("unsupported AlertConditionKind: " + value);
    }

    void validate(String path, List<ValidatorError> errors) {
        Rules.oneOf(path, this, List.of(values()), errors);
    }
}
