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
public class SLO implements V2alphaObject {
    private Version apiVersion = Version.V2ALPHA;
    private Kind kind = Kind.SLO;
    private Metadata metadata;
    private SLOSpec spec = new SLOSpec();

    public SLO() {}

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
    public static class SLOSpec {
        private String description;
        private String serviceRef;
        private SLOSLIInline sli;
        private String sliRef;
        private String budgetingMethod;
        private List<SLOTimeWindow> timeWindow = new ArrayList<>();
        private List<SLOObjective> objectives = new ArrayList<>();
        private List<SLOAlertPolicy> alertPolicies = new ArrayList<>();

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getServiceRef() {
            return serviceRef;
        }

        public void setServiceRef(String serviceRef) {
            this.serviceRef = serviceRef;
        }

        public SLOSLIInline getSli() {
            return sli;
        }

        public void setSli(SLOSLIInline sli) {
            this.sli = sli;
        }

        public String getSliRef() {
            return sliRef;
        }

        public void setSliRef(String sliRef) {
            this.sliRef = sliRef;
        }

        public String getBudgetingMethod() {
            return budgetingMethod;
        }

        public void setBudgetingMethod(String budgetingMethod) {
            this.budgetingMethod = budgetingMethod;
        }

        public List<SLOTimeWindow> getTimeWindow() {
            return timeWindow;
        }

        public void setTimeWindow(List<SLOTimeWindow> timeWindow) {
            this.timeWindow = timeWindow;
        }

        public List<SLOObjective> getObjectives() {
            return objectives;
        }

        public void setObjectives(List<SLOObjective> objectives) {
            this.objectives = objectives;
        }

        public List<SLOAlertPolicy> getAlertPolicies() {
            return alertPolicies;
        }

        public void setAlertPolicies(List<SLOAlertPolicy> alertPolicies) {
            this.alertPolicies = alertPolicies;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SLOSLIInline {
        private Metadata metadata;
        private SLI.SLISpec spec;

        public Metadata getMetadata() {
            return metadata;
        }

        public void setMetadata(Metadata metadata) {
            this.metadata = metadata;
        }

        public SLI.SLISpec getSpec() {
            return spec;
        }

        public void setSpec(SLI.SLISpec spec) {
            this.spec = spec;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SLOObjective {
        private String displayName;
        private Operator op;
        private Double value;
        private Double target;
        private Double targetPercent;
        private Double timeSliceTarget;
        private DurationShorthand timeSliceWindow;
        private SLOSLIInline sli;
        private String sliRef;
        private Double compositeWeight;

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public Operator getOp() {
            return op;
        }

        public void setOp(Operator op) {
            this.op = op;
        }

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }

        public Double getTarget() {
            return target;
        }

        public void setTarget(Double target) {
            this.target = target;
        }

        public Double getTargetPercent() {
            return targetPercent;
        }

        public void setTargetPercent(Double targetPercent) {
            this.targetPercent = targetPercent;
        }

        public Double getTimeSliceTarget() {
            return timeSliceTarget;
        }

        public void setTimeSliceTarget(Double timeSliceTarget) {
            this.timeSliceTarget = timeSliceTarget;
        }

        public DurationShorthand getTimeSliceWindow() {
            return timeSliceWindow;
        }

        public void setTimeSliceWindow(DurationShorthand timeSliceWindow) {
            this.timeSliceWindow = timeSliceWindow;
        }

        public SLOSLIInline getSli() {
            return sli;
        }

        public void setSli(SLOSLIInline sli) {
            this.sli = sli;
        }

        public String getSliRef() {
            return sliRef;
        }

        public void setSliRef(String sliRef) {
            this.sliRef = sliRef;
        }

        public Double getCompositeWeight() {
            return compositeWeight;
        }

        public void setCompositeWeight(Double compositeWeight) {
            this.compositeWeight = compositeWeight;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SLOTimeWindow {
        private DurationShorthand duration;
        @com.fasterxml.jackson.annotation.JsonProperty("isRolling")
        private boolean isRolling;
        private SLOCalendar calendar;

        public DurationShorthand getDuration() {
            return duration;
        }

        public void setDuration(DurationShorthand duration) {
            this.duration = duration;
        }

        public boolean isRolling() {
            return isRolling;
        }

        public void setRolling(boolean rolling) {
            isRolling = rolling;
        }

        public SLOCalendar getCalendar() {
            return calendar;
        }

        public void setCalendar(SLOCalendar calendar) {
            this.calendar = calendar;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SLOCalendar {
        private String startTime;
        private String timeZone;

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getTimeZone() {
            return timeZone;
        }

        public void setTimeZone(String timeZone) {
            this.timeZone = timeZone;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SLOAlertPolicy {
        private String alertPolicyRef;
        private Kind kind;
        private Metadata metadata;
        private AlertPolicy.AlertPolicySpec spec;

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

        public AlertPolicy.AlertPolicySpec getSpec() {
            return spec;
        }

        public void setSpec(AlertPolicy.AlertPolicySpec spec) {
            this.spec = spec;
        }
    }
}
