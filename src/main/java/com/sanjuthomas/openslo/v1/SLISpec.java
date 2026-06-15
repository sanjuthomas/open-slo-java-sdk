package com.sanjuthomas.openslo.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sanjuthomas.openslo.validation.Rules;
import com.sanjuthomas.openslo.validation.ValidatorError;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
public class SLISpec {
    private String description;
    private SLIMetricSpec thresholdMetric;
    private SLIRatioMetric ratioMetric;

    public SLISpec() {}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SLIMetricSpec getThresholdMetric() {
        return thresholdMetric;
    }

    public void setThresholdMetric(SLIMetricSpec thresholdMetric) {
        this.thresholdMetric = thresholdMetric;
    }

    public SLIRatioMetric getRatioMetric() {
        return ratioMetric;
    }

    public void setRatioMetric(SLIRatioMetric ratioMetric) {
        this.ratioMetric = ratioMetric;
    }

    void validate(String path, List<ValidatorError> errors) {
        Rules.maxLength(path + ".description", description, 1050, errors);
        Rules.mutuallyExclusive(
                path,
                true,
                Rules.fieldsMap("thresholdMetric", thresholdMetric, "ratioMetric", ratioMetric),
                errors);
        if (thresholdMetric != null) {
            thresholdMetric.validate(path + ".thresholdMetric", errors);
        }
        if (ratioMetric != null) {
            ratioMetric.validate(path + ".ratioMetric", errors);
        }
    }
}
