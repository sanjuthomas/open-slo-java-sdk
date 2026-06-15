package com.sanjuthomas.openslo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.sanjuthomas.openslo.validation.ValidationException;

public enum Version {
    V1ALPHA("openslo/v1alpha"),
    V1("openslo/v1"),
    V2ALPHA("openslo.com/v2alpha");

    private final String value;

    Version(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Version fromValue(String value) {
        for (Version version : values()) {
            if (version.value.equals(value)) {
                return version;
            }
        }
        throw new IllegalArgumentException("unsupported Version: " + value);
    }

    public void validate() throws ValidationException {
        // All enum values are supported.
    }

    @Override
    public String toString() {
        return value;
    }
}
