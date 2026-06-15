package com.sanjuthomas.openslo.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sanjuthomas.openslo.validation.Rules;
import com.sanjuthomas.openslo.validation.ValidatorError;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
public class SLIMetricSource {
    private String metricSourceRef;
    private String type;
    private Map<String, Object> spec;

    public SLIMetricSource() {}

    public String getMetricSourceRef() {
        return metricSourceRef;
    }

    public void setMetricSourceRef(String metricSourceRef) {
        this.metricSourceRef = metricSourceRef;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getSpec() {
        return spec;
    }

    public void setSpec(Map<String, Object> spec) {
        this.spec = spec;
    }

    void validate(String path, List<ValidatorError> errors) {
        Rules.dnsLabel(path + ".metricSourceRef", metricSourceRef, errors);
        Rules.required(path + ".spec", spec, errors);
        Rules.mapMinLength(path + ".spec", spec, 1, errors);
    }
}
