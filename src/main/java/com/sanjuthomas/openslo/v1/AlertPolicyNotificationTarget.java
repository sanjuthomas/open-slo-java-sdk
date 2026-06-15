package com.sanjuthomas.openslo.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sanjuthomas.openslo.Kind;
import com.sanjuthomas.openslo.validation.Rules;
import com.sanjuthomas.openslo.validation.ValidatorError;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
public class AlertPolicyNotificationTarget {
    private String targetRef;
    private Kind kind;
    private Metadata metadata;
    private AlertNotificationTargetSpec spec;

    public AlertPolicyNotificationTarget() {}

    public String getTargetRef() {
        return targetRef;
    }

    public void setTargetRef(String targetRef) {
        this.targetRef = targetRef;
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

    public AlertNotificationTargetSpec getSpec() {
        return spec;
    }

    public void setSpec(AlertNotificationTargetSpec spec) {
        this.spec = spec;
    }

    public boolean isRef() {
        return targetRef != null && !targetRef.isEmpty();
    }

    public boolean isInline() {
        return kind != null && metadata != null && spec != null;
    }

    public void clearRef() {
        targetRef = null;
    }

    public void clearInline() {
        kind = null;
        metadata = null;
        spec = null;
    }

    private boolean hasRef() {
        return targetRef != null && !targetRef.isEmpty();
    }

    private boolean hasInline() {
        return spec != null;
    }

    void validate(String path, List<ValidatorError> errors) {
        Rules.mutuallyExclusive(
                path,
                true,
                Rules.fieldsMap("targetRef", hasRef() ? targetRef : null, "spec", spec),
                errors);

        if (hasRef()) {
            Rules.required(path + ".targetRef", targetRef, errors);
            Rules.dnsLabel(path + ".targetRef", targetRef, errors);
        }

        if (hasInline()) {
            Rules.required(path + ".kind", kind, errors);
            Rules.eq(path + ".kind", kind, Kind.ALERT_NOTIFICATION_TARGET, errors);
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
