package com.sanjuthomas.openslo;

import com.sanjuthomas.openslo.validation.ValidationException;

/**
 * Represents a generic OpenSLO object definition.
 * All OpenSLO objects implement this interface.
 */
public interface OpenSloObject {
    Version getVersion();

    Kind getKind();

    String getName();

    void validate() throws ValidationException;

    String displayName();
}
