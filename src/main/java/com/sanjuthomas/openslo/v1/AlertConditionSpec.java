package com.sanjuthomas.openslo.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sanjuthomas.openslo.validation.Rules;
import com.sanjuthomas.openslo.validation.ValidatorError;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
public class AlertConditionSpec {
    private String severity;
    private AlertConditionType condition;
    private String description;

    public AlertConditionSpec() {}

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    void validate(String path, List<ValidatorError> errors) {
        Rules.maxLength(path + ".description", description, 1050, errors);
        Rules.required(path + ".severity", severity, errors);
        Rules.required(path + ".condition", condition, errors);
        if (condition != null) {
            condition.validate(path + ".condition", errors);
        }
    }
}
