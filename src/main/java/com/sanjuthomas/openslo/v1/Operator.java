package com.sanjuthomas.openslo.v1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.sanjuthomas.openslo.validation.Rules;
import com.sanjuthomas.openslo.validation.ValidatorError;

import java.util.List;

public enum Operator {
    GT("gt"),
    LT("lt"),
    GTE("gte"),
    LTE("lte");

    private final String value;

    Operator(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Operator fromValue(String value) {
        for (Operator operator : values()) {
            if (operator.value.equals(value)) {
                return operator;
            }
        }
        throw new IllegalArgumentException("unsupported Operator: " + value);
    }

    public void validate(String path, List<ValidatorError> errors) {
        Rules.oneOf(path, this, List.of(values()), errors);
    }
}
