package com.sanjuthomas.openslo.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sanjuthomas.openslo.validation.Rules;
import com.sanjuthomas.openslo.validation.ValidatorError;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
public class SLOSpec {
    private String description;
    private String service;
    private SLOIndicatorInline indicator;
    private String indicatorRef;
    private SLOBudgetingMethod budgetingMethod;
    private List<SLOTimeWindow> timeWindow;
    private List<SLOObjective> objectives;
    private List<SLOAlertPolicy> alertPolicies;

    public SLOSpec() {}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
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

    public SLOBudgetingMethod getBudgetingMethod() {
        return budgetingMethod;
    }

    public void setBudgetingMethod(SLOBudgetingMethod budgetingMethod) {
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

    public boolean hasCompositeObjectives() {
        if (objectives == null) {
            return false;
        }
        for (SLOObjective objective : objectives) {
            if (objective.getIndicator() != null
                    || (objective.getIndicatorRef() != null && !objective.getIndicatorRef().isEmpty())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasInlinedRatioMetric() {
        return indicator != null
                && indicator.getSpec() != null
                && indicator.getSpec().getRatioMetric() != null;
    }

    private boolean hasInlinedThresholdMetric() {
        return indicator != null
                && indicator.getSpec() != null
                && indicator.getSpec().getThresholdMetric() != null;
    }

    void validate(String path, List<ValidatorError> errors) {
        V1Validators.validateSloIndicatorRule(this, path, errors);

        boolean hasIndicatorAtSpecLevel = indicator != null
                || (indicatorRef != null && !indicatorRef.isEmpty());
        if (hasIndicatorAtSpecLevel) {
            V1Validators.validateSloIndicator(path, indicator, indicatorRef, errors);
        }

        if (budgetingMethod == SLOBudgetingMethod.TIMESLICES && objectives != null) {
            for (int i = 0; i < objectives.size(); i++) {
                objectives.get(i).validateTimeslicesObjective(path + ".objectives[" + i + "]", errors);
            }
        }

        if (budgetingMethod == SLOBudgetingMethod.RATIO_TIMESLICES && objectives != null) {
            for (int i = 0; i < objectives.size(); i++) {
                objectives.get(i).validateRatioTimeslicesObjective(path + ".objectives[" + i + "]", errors);
            }
        }

        Rules.maxLength(path + ".description", description, 1050, errors);
        Rules.required(path + ".service", service, errors);
        Rules.required(path + ".budgetingMethod", budgetingMethod, errors);
        if (budgetingMethod != null) {
            budgetingMethod.validate(path + ".budgetingMethod", errors);
        }

        Rules.sliceLength(path + ".timeWindow", timeWindow, 1, 1, errors);
        if (timeWindow != null) {
            for (int i = 0; i < timeWindow.size(); i++) {
                timeWindow.get(i).validate(path + ".timeWindow[" + i + "]", errors);
            }
        }

        if (alertPolicies != null) {
            for (int i = 0; i < alertPolicies.size(); i++) {
                alertPolicies.get(i).validate(path + ".alertPolicies[" + i + "]", errors);
            }
        }

        if (objectives != null) {
            for (int i = 0; i < objectives.size(); i++) {
                String objPath = path + ".objectives[" + i + "]";
                SLOObjective objective = objectives.get(i);
                objective.validateObjective(objPath, errors);

                if (hasInlinedRatioMetric()) {
                    objective.validateRatioObjectiveWhenInlinedSli(objPath, errors);
                }
                if (hasInlinedThresholdMetric()) {
                    objective.validateThresholdObjectiveWhenInlinedSli(objPath, errors);
                }
                if (hasCompositeObjectives()) {
                    objective.validateCompositeObjective(objPath, errors);
                }
            }
        }
    }
}
