package com.sanjuthomas.openslo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.sanjuthomas.openslo.validation.ValidationException;

public enum Kind {
    SLO("SLO"),
    SLI("SLI"),
    DATA_SOURCE("DataSource"),
    SERVICE("Service"),
    ALERT_POLICY("AlertPolicy"),
    ALERT_CONDITION("AlertCondition"),
    ALERT_NOTIFICATION_TARGET("AlertNotificationTarget");

    private final String value;

    Kind(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Kind fromValue(String value) {
        for (Kind kind : values()) {
            if (kind.value.equals(value)) {
                return kind;
            }
        }
        throw new IllegalArgumentException("unsupported Kind: " + value);
    }

    public void validate() throws ValidationException {
        // All enum values are supported.
    }

    @Override
    public String toString() {
        return value;
    }
}
