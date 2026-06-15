package com.sanjuthomas.openslo.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sanjuthomas.openslo.validation.Rules;
import com.sanjuthomas.openslo.validation.ValidatorError;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
public class SLIMetricSpec {
    private SLIMetricSource metricSource;

    public SLIMetricSpec() {}

    public SLIMetricSource getMetricSource() {
        return metricSource;
    }

    public void setMetricSource(SLIMetricSource metricSource) {
        this.metricSource = metricSource;
    }

    void validate(String path, List<ValidatorError> errors) {
        Rules.required(path + ".metricSource", metricSource, errors);
        if (metricSource != null) {
            metricSource.validate(path + ".metricSource", errors);
        }
    }
}
