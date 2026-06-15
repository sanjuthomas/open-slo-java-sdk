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
public class SLO implements V1Object {
    private Version apiVersion = Version.V1;
    private Kind kind = Kind.SLO;
    private Metadata metadata;
    private SLOSpec spec = new SLOSpec();

    public SLO() {}

    public SLO(Metadata metadata, SLOSpec spec) {
        this.metadata = metadata;
        this.spec = spec;
    }

    public static SLO newSlo(Metadata metadata, SLOSpec spec) {
        return new SLO(metadata, spec);
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

    public SLOSpec getSpec() {
        return spec;
    }

    public void setSpec(SLOSpec spec) {
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

    public boolean isComposite() {
        return spec != null && spec.hasCompositeObjectives();
    }

    @Override
    public void validate() throws ValidationException {
        List<ValidatorError> errors = new ArrayList<>();
        V1Validators.validateApiVersion("", apiVersion, errors);
        V1Validators.validateKind("", kind, Kind.SLO, errors);
        V1Validators.validateMetadata("metadata", metadata, errors);
        Rules.required("spec", spec, errors);
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
