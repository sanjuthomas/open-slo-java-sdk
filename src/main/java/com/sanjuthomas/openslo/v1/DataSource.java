package com.sanjuthomas.openslo.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sanjuthomas.openslo.Kind;
import com.sanjuthomas.openslo.Version;
import com.sanjuthomas.openslo.internal.ObjectNames;
import com.sanjuthomas.openslo.validation.Rules;
import com.sanjuthomas.openslo.validation.ValidationException;
import com.sanjuthomas.openslo.validation.ValidatorError;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
public class DataSource implements V1Object {
    private Version apiVersion = Version.V1;
    private Kind kind = Kind.DATA_SOURCE;
    private Metadata metadata;
    private DataSourceSpec spec = new DataSourceSpec();

    public DataSource() {}

    public DataSource(Metadata metadata, DataSourceSpec spec) {
        this.metadata = metadata;
        this.spec = spec;
    }

    public static DataSource newDataSource(Metadata metadata, DataSourceSpec spec) {
        return new DataSource(metadata, spec);
    }

    @Override
    public Version getVersion() {
        return apiVersion;
    }

    public void setApiVersion(Version apiVersion) {
        this.apiVersion = apiVersion;
    }

    @Override
    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    @Override
    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public DataSourceSpec getSpec() {
        return spec;
    }

    public void setSpec(DataSourceSpec spec) {
        this.spec = spec;
    }

    @Override
    public String getName() {
        return metadata != null ? metadata.getName() : null;
    }

    @Override
    public String displayName() {
        return metadata != null && metadata.getDisplayName() != null
                ? metadata.getDisplayName()
                : getName();
    }

    @Override
    public void validate() throws ValidationException {
        List<ValidatorError> errors = new ArrayList<>();
        V1Validators.validateApiVersion("", apiVersion, errors);
        V1Validators.validateKind("", kind, Kind.DATA_SOURCE, errors);
        V1Validators.validateMetadata("metadata", metadata, errors);
        if (spec != null) {
            spec.validate("spec", errors);
        }
        Rules.throwIfErrors(errors);
    }

    @Override
    public String toString() {
        return ObjectNames.getObjectName(this);
    }
}
