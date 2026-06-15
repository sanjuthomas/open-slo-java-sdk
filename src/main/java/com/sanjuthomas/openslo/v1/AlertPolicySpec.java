package com.sanjuthomas.openslo.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sanjuthomas.openslo.validation.Rules;
import com.sanjuthomas.openslo.validation.ValidatorError;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
public class AlertPolicySpec {
    private String description;
    private boolean alertWhenNoData;
    private boolean alertWhenBreaching;
    private boolean alertWhenResolved;
    private List<AlertPolicyCondition> conditions;
    private List<AlertPolicyNotificationTarget> notificationTargets;

    public AlertPolicySpec() {}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAlertWhenNoData() {
        return alertWhenNoData;
    }

    public void setAlertWhenNoData(boolean alertWhenNoData) {
        this.alertWhenNoData = alertWhenNoData;
    }

    public boolean isAlertWhenBreaching() {
        return alertWhenBreaching;
    }

    public void setAlertWhenBreaching(boolean alertWhenBreaching) {
        this.alertWhenBreaching = alertWhenBreaching;
    }

    public boolean isAlertWhenResolved() {
        return alertWhenResolved;
    }

    public void setAlertWhenResolved(boolean alertWhenResolved) {
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

    void validate(String path, List<ValidatorError> errors) {
        Rules.maxLength(path + ".description", description, 1050, errors);
        Rules.sliceLength(path + ".conditions", conditions, 1, 1, errors);
        if (conditions != null) {
            for (int i = 0; i < conditions.size(); i++) {
                conditions.get(i).validate(path + ".conditions[" + i + "]", errors);
            }
        }
        Rules.sliceMinLength(path + ".notificationTargets", notificationTargets, 1, errors);
        if (notificationTargets != null) {
            for (int i = 0; i < notificationTargets.size(); i++) {
                notificationTargets.get(i).validate(path + ".notificationTargets[" + i + "]", errors);
            }
        }
    }
}
