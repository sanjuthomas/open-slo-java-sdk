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
public class AlertCondition implements V2alphaObject {
    private Version apiVersion = Version.V2ALPHA;
    private Kind kind = Kind.ALERT_CONDITION;
    private Metadata metadata;
    private AlertConditionSpec spec = new AlertConditionSpec();

    public AlertCondition() {}

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

    public AlertConditionSpec getSpec() {
        return spec;
    }

    public void setSpec(AlertConditionSpec spec) {
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
    public static class AlertConditionSpec {
        private String description;
        private String severity;
        private AlertConditionType condition;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getSeverity() {
            return severity;
        }

        public void setSeverity(String severity) {
            this.severity = severity;
        }

        public AlertConditionType getCondition() {
            return condition;
        }

        public void setCondition(AlertConditionType condition) {
            this.condition = condition;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AlertConditionType {
        private String kind;
        private Operator op;
        private Double threshold;
        private DurationShorthand lookbackWindow;
        private DurationShorthand alertAfter;

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public Operator getOp() {
            return op;
        }

        public void setOp(Operator op) {
            this.op = op;
        }

        public Double getThreshold() {
            return threshold;
        }

        public void setThreshold(Double threshold) {
            this.threshold = threshold;
        }

        public DurationShorthand getLookbackWindow() {
            return lookbackWindow;
        }

        public void setLookbackWindow(DurationShorthand lookbackWindow) {
            this.lookbackWindow = lookbackWindow;
        }

        public DurationShorthand getAlertAfter() {
            return alertAfter;
        }

        public void setAlertAfter(DurationShorthand alertAfter) {
            this.alertAfter = alertAfter;
        }
    }
}
