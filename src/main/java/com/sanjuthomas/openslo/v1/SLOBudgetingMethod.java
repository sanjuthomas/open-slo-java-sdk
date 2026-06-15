package com.sanjuthomas.openslo.v1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.sanjuthomas.openslo.validation.Rules;
import com.sanjuthomas.openslo.validation.ValidatorError;

import java.util.List;

public enum SLOBudgetingMethod {
    OCCURRENCES("Occurrences"),
    TIMESLICES("Timeslices"),
    RATIO_TIMESLICES("RatioTimeslices");

    private final String value;

    SLOBudgetingMethod(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static SLOBudgetingMethod fromValue(String value) {
        for (SLOBudgetingMethod method : values()) {
            if (method.value.equals(value)) {
                return method;
            }
        }
        throw new IllegalArgumentException("unsupported SLOBudgetingMethod: " + value);
    }

    void validate(String path, List<ValidatorError> errors) {
        Rules.oneOf(path, this, List.of(values()), errors);
    }
}
