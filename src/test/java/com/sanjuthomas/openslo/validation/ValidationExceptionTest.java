package com.sanjuthomas.openslo.validation;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationExceptionTest {

    @Test
    void validationExceptionFormatsMultipleErrors() {
        ValidationException ex = new ValidationException(List.of(
                new ValidatorError("first error"),
                new ValidatorError("second error")));
        assertTrue(ex.getMessage().contains("first error"));
        assertTrue(ex.getMessage().contains("second error"));
        assertEquals(2, ex.getErrors().size());
    }

    @Test
    void validationExceptionSingleErrorConstructor() {
        Exception ex = new ValidationException(new ValidatorError("only error"));
        assertInstanceOf(ValidationException.class, ex);
        assertEquals("only error", ex.getMessage());
    }
}
