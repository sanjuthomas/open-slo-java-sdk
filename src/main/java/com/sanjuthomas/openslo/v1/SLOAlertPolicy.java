package com.sanjuthomas.openslo.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sanjuthomas.openslo.Kind;
import com.sanjuthomas.openslo.validation.Rules;
import com.sanjuthomas.openslo.validation.ValidatorError;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
public class SLOAlertPolicy {
    private String alertPolicyRef;
    private Kind kind;
    private Metadata metadata;
    private AlertPolicySpec spec;

    public SLOAlertPolicy() {}

    public String getAlertPolicyRef() {
        return alertPolicyRef;
    }

    public void setAlertPolicyRef(String alertPolicyRef) {
        this.alertPolicyRef = alertPolicyRef;
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

    public AlertPolicySpec getSpec() {
        return spec;
    }

    public void setSpec(AlertPolicySpec spec) {
        this.spec = spec;
    }

    public boolean isRef() {
        return alertPolicyRef != null && !alertPolicyRef.isEmpty();
    }

    public boolean isInline() {
        return kind != null && metadata != null && spec != null;
    }

    public void clearRef() {
        alertPolicyRef = null;
    }

    public void clearInline() {
        kind = null;
        metadata = null;
        spec = null;
    }

    private boolean hasRef() {
        return alertPolicyRef != null && !alertPolicyRef.isEmpty();
    }

    private boolean hasInline() {
        return spec != null;
    }

    void validate(String path, List<ValidatorError> errors) {
        Rules.mutuallyExclusive(
                path,
                true,
                Rules.fieldsMap("alertPolicyRef", hasRef() ? alertPolicyRef : null, "spec", spec),
                errors);

        if (hasRef()) {
            Rules.required(path + ".alertPolicyRef", alertPolicyRef, errors);
            Rules.dnsLabel(path + ".alertPolicyRef", alertPolicyRef, errors);
        }

        if (hasInline()) {
            Rules.required(path + ".kind", kind, errors);
            Rules.eq(path + ".kind", kind, Kind.ALERT_POLICY, errors);
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
