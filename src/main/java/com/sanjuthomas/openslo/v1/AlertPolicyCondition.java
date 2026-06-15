package com.sanjuthomas.openslo.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sanjuthomas.openslo.Kind;
import com.sanjuthomas.openslo.validation.Rules;
import com.sanjuthomas.openslo.validation.ValidatorError;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
public class AlertPolicyCondition {
    private String conditionRef;
    private Kind kind;
    private Metadata metadata;
    private AlertConditionSpec spec;

    public AlertPolicyCondition() {}

    public String getConditionRef() {
        return conditionRef;
    }

    public void setConditionRef(String conditionRef) {
        this.conditionRef = conditionRef;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public AlertConditionSpec getSpec() {
        return spec;
    }

    public void setSpec(AlertConditionSpec spec) {
        this.spec = spec;
    }

    public boolean isRef() {
        return conditionRef != null && !conditionRef.isEmpty();
    }

    public boolean isInline() {
        return kind != null && metadata != null && spec != null;
    }

    public void clearRef() {
        conditionRef = null;
    }

    public void clearInline() {
        kind = null;
        metadata = null;
        spec = null;
    }

    private boolean hasRef() {
        return conditionRef != null && !conditionRef.isEmpty();
    }

    private boolean hasInline() {
        return spec != null;
    }

    void validate(String path, List<ValidatorError> errors) {
        Rules.mutuallyExclusive(
                path,
                true,
                Rules.fieldsMap("conditionRef", hasRef() ? conditionRef : null, "spec", spec),
                errors);

        if (hasRef()) {
            Rules.required(path + ".conditionRef", conditionRef, errors);
            Rules.dnsLabel(path + ".conditionRef", conditionRef, errors);
        }

        if (hasInline()) {
            Rules.required(path + ".kind", kind, errors);
            Rules.eq(path + ".kind", kind, Kind.ALERT_CONDITION, errors);
            Rules.required(path + ".metadata", metadata, errors);
            if (metadata != null) {
                metadata.validate(path + ".metadata", errors);
            }
            Rules.required(path + ".spec", spec, errors);
            if (spec != null) {
                spec.validate(path + ".spec", errors);
            }
        }
    }
}
