package com.sanjuthomas.openslo.v2alpha;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sanjuthomas.openslo.validation.Rules;
import com.sanjuthomas.openslo.validation.ValidatorError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
public class Metadata {
    private String name;
    private Map<String, String> labels = new HashMap<>();
    private Map<String, String> annotations = new HashMap<>();

    public Metadata() {}

    public Metadata(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    public Map<String, String> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Map<String, String> annotations) {
        this.annotations = annotations;
    }

    void validate(String path, List<ValidatorError> errors) {
        Rules.validateMetadata(path, name, null, labels, annotations, false, true, true, errors);
        if (labels != null) {
            labels.forEach((key, value) -> Rules.labelValueV2(path + ".labels." + key, value, errors));
        }
    }
}
