package com.sanjuthomas.openslo.v2alpha;

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
public class AlertPolicy implements V2alphaObject {
    private Version apiVersion = Version.V2ALPHA;
    private Kind kind = Kind.ALERT_POLICY;
    private Metadata metadata;
    private AlertPolicySpec spec = new AlertPolicySpec();

    public AlertPolicy() {}

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

    public AlertPolicySpec getSpec() {
        return spec;
    }

    public void setSpec(AlertPolicySpec spec) {
        this.spec = spec;
    }

    @Override
    public String getName() {
        return metadata != null ? metadata.getName() : null;
    }

    @Override
    public String displayName() {
        return getName();
    }

    @Override
    public void validate() throws ValidationException {
        List<ValidatorError> errors = new ArrayList<>();
        if (metadata != null) {
            metadata.validate("metadata", errors);
        }
        Rules.throwIfErrors(errors);
    }

    @Override
    public String toString() {
        return ObjectNames.getObjectName(this);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AlertPolicySpec {
        private String description;
        private Boolean alertWhenNoData;
        private Boolean alertWhenBreaching;
        private Boolean alertWhenResolved;
        private List<AlertPolicyCondition> conditions = new ArrayList<>();
        private List<AlertPolicyNotificationTarget> notificationTargets = new ArrayList<>();

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Boolean getAlertWhenNoData() {
            return alertWhenNoData;
        }

        public void setAlertWhenNoData(Boolean alertWhenNoData) {
            this.alertWhenNoData = alertWhenNoData;
        }

        public Boolean getAlertWhenBreaching() {
            return alertWhenBreaching;
        }

        public void setAlertWhenBreaching(Boolean alertWhenBreaching) {
            this.alertWhenBreaching = alertWhenBreaching;
        }

        public Boolean getAlertWhenResolved() {
            return alertWhenResolved;
        }

        public void setAlertWhenResolved(Boolean alertWhenResolved) {
            this.alertWhenResolved = alertWhenResolved;
        }

        public List<AlertPolicyCondition> getConditions() {
            return conditions;
        }

        public void setConditions(List<AlertPolicyCondition> conditions) {
            this.conditions = conditions;
        }

        public List<AlertPolicyNotificationTarget> getNotificationTargets() {
            return notificationTargets;
        }

        public void setNotificationTargets(List<AlertPolicyNotificationTarget> notificationTargets) {
            this.notificationTargets = notificationTargets;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AlertPolicyCondition {
        private String conditionRef;
        private Kind kind;
        private Metadata metadata;
        private AlertCondition.AlertConditionSpec spec;

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

        public AlertCondition.AlertConditionSpec getSpec() {
            return spec;
        }

        public void setSpec(AlertCondition.AlertConditionSpec spec) {
            this.spec = spec;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AlertPolicyNotificationTarget {
        private String targetRef;
        private Kind kind;
        private Metadata metadata;
        private AlertNotificationTarget.AlertNotificationTargetSpec spec;

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

        public AlertNotificationTarget.AlertNotificationTargetSpec getSpec() {
            return spec;
        }

        public void setSpec(AlertNotificationTarget.AlertNotificationTargetSpec spec) {
            this.spec = spec;
        }
    }
}
