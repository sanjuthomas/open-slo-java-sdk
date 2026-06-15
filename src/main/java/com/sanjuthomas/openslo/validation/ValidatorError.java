package com.sanjuthomas.openslo.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ValidatorError {
    private final String message;
    private String name;
    private String propertyPath;
    private Integer sliceIndex;

    public ValidatorError(String message) {
        this.message = Objects.requireNonNull(message);
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

    public ValidatorError withName(String name) {
        this.name = name;
        return this;
    }

    public String getPropertyPath() {
        return propertyPath;
    }

    public ValidatorError withPropertyPath(String propertyPath) {
        this.propertyPath = propertyPath;
        return this;
    }

    public Integer getSliceIndex() {
        return sliceIndex;
    }

    public ValidatorError withSliceIndex(Integer sliceIndex) {
        this.sliceIndex = sliceIndex;
        return this;
    }
}
