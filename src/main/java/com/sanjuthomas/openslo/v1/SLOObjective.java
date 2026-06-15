package com.sanjuthomas.openslo.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sanjuthomas.openslo.validation.Rules;
import com.sanjuthomas.openslo.validation.ValidatorError;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
public class SLOObjective {
    private String displayName;

    @JsonProperty("op")
    private Operator operator;

    private Double value;
    private Double target;
    private Double targetPercent;
    private Double timeSliceTarget;
    private DurationShorthand timeSliceWindow;
    private SLOIndicatorInline indicator;
    private String indicatorRef;
    private Double compositeWeight;

    public SLOObjective() {}

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
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

    public SLOIndicatorInline getIndicator() {
        return indicator;
    }

    public void setIndicator(SLOIndicatorInline indicator) {
        this.indicator = indicator;
    }

    public String getIndicatorRef() {
        return indicatorRef;
    }

    public void setIndicatorRef(String indicatorRef) {
        this.indicatorRef = indicatorRef;
    }

    public Double getCompositeWeight() {
        return compositeWeight;
    }

    public void setCompositeWeight(Double compositeWeight) {
        this.compositeWeight = compositeWeight;
    }

    void validateObjective(String path, List<ValidatorError> errors) {
        Rules.mutuallyExclusive(
                path,
                true,
                Rules.fieldsMap("target", target, "targetPercent", targetPercent),
                errors);
        if (target != null) {
            Rules.gte(path + ".target", target, 0.0, errors);
            Rules.lt(path + ".target", target, 1.0, errors);
        }
        if (targetPercent != null) {
            Rules.gte(path + ".targetPercent", targetPercent, 0.0, errors);
            Rules.lt(path + ".targetPercent", targetPercent, 100.0, errors);
        }
    }

    void validateThresholdObjectiveWhenInlinedSli(String path, List<ValidatorError> errors) {
        Rules.required(path + ".value", value, errors);
        Rules.required(path + ".op", operator, errors);
        if (operator != null) {
            operator.validate(path + ".op", errors);
        }
    }

    void validateRatioObjectiveWhenInlinedSli(String path, List<ValidatorError> errors) {
        Rules.forbidden(path + ".value", value, errors);
        Rules.forbidden(path + ".op", operator, errors);
    }

    void validateCompositeObjective(String path, List<ValidatorError> errors) {
        V1Validators.validateSloIndicator(path, indicator, indicatorRef, errors);
        if (compositeWeight != null) {
            Rules.gt(path + ".compositeWeight", compositeWeight, 0.0, errors);
        }
    }

    void validateTimeslicesObjective(String path, List<ValidatorError> errors) {
        Rules.required(path + ".timeSliceTarget", timeSliceTarget, errors);
        if (timeSliceTarget != null) {
            Rules.gt(path + ".timeSliceTarget", timeSliceTarget, 0.0, errors);
            Rules.lte(path + ".timeSliceTarget", timeSliceTarget, 1.0, errors);
        }
        V1Validators.validateTimeSliceWindow(path + ".timeSliceWindow", timeSliceWindow, errors);
    }

    void validateRatioTimeslicesObjective(String path, List<ValidatorError> errors) {
        V1Validators.validateTimeSliceWindow(path + ".timeSliceWindow", timeSliceWindow, errors);
    }
}
