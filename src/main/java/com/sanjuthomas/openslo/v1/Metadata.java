package com.sanjuthomas.openslo.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
public class Metadata {
    private String name;
    private String displayName;
    private Map<String, Label> labels = new HashMap<>();
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Map<String, Label> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, Label> labels) {
        this.labels = labels;
    }

    public Map<String, String> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Map<String, String> annotations) {
        this.annotations = annotations;
    }

    void validate(String path, java.util.List<com.sanjuthomas.openslo.validation.ValidatorError> errors) {
        com.sanjuthomas.openslo.validation.Rules.validateMetadata(
                path, name, displayName, labels, annotations, true, true, true, errors);
    }
}
