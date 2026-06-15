package com.sanjuthomas.openslo.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ValidationException extends Exception {
    private final List<ValidatorError> errors;

    public ValidationException(List<ValidatorError> errors) {
        super(formatMessage(errors));
        this.errors = List.copyOf(errors);
    }

    public ValidationException(ValidatorError error) {
        this(List.of(error));
    }

    public List<ValidatorError> getErrors() {
        return errors;
    }

    private static String formatMessage(List<ValidatorError> errors) {
        return errors.stream()
                .map(ValidatorError::getMessage)
                .collect(Collectors.joining("; "));
    }
}
