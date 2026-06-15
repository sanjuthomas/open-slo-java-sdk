package com.sanjuthomas.openslo.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sanjuthomas.openslo.validation.Rules;
import com.sanjuthomas.openslo.validation.ValidatorError;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
public class AlertConditionType {
    private AlertConditionKind kind;

    @JsonProperty("op")
    private Operator operator;

    private Double threshold;
    private DurationShorthand lookbackWindow;
    private DurationShorthand alertAfter;

    public AlertConditionType() {}

    public AlertConditionKind getKind() {
        return kind;
    }

    public void setKind(AlertConditionKind kind) {
        this.kind = kind;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
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

    void validate(String path, List<ValidatorError> errors) {
        Rules.required(path + ".kind", kind, errors);
        if (kind != null) {
            kind.validate(path + ".kind", errors);
        }

        if (kind == AlertConditionKind.BURN_RATE) {
            Rules.required(path + ".op", operator, errors);
            if (operator != null) {
                operator.validate(path + ".op", errors);
            }
            Rules.required(path + ".threshold", threshold, errors);
            Rules.required(path + ".lookbackWindow", lookbackWindow, errors);
            if (lookbackWindow != null) {
                lookbackWindow.validate(path + ".lookbackWindow", errors);
            }
            if (alertAfter != null) {
                alertAfter.validate(path + ".alertAfter", errors);
            }
        }
    }
}
