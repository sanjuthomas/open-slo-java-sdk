package com.sanjuthomas.openslo.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sanjuthomas.openslo.validation.Rules;
import com.sanjuthomas.openslo.validation.ValidatorError;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
public class SLOIndicatorInline {
    private Metadata metadata;
    private SLISpec spec;

    public SLOIndicatorInline() {}

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public SLISpec getSpec() {
        return spec;
    }

    public void setSpec(SLISpec spec) {
        this.spec = spec;
    }

    void validate(String path, List<ValidatorError> errors) {
        Rules.required(path + ".metadata", metadata, errors);
        if (metadata != null) {
            metadata.validate(path + ".metadata", errors);
        }
        if (spec != null) {
            spec.validate(path + ".spec", errors);
        }
    }
}
