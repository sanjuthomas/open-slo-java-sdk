package com.sanjuthomas.openslo.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.sanjuthomas.openslo.validation.Rules;
import com.sanjuthomas.openslo.validation.ValidatorError;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
public class DataSourceSpec {
    private String description;
    private String type;
    private JsonNode connectionDetails;

    public DataSourceSpec() {}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JsonNode getConnectionDetails() {
        return connectionDetails;
    }

    public void setConnectionDetails(JsonNode connectionDetails) {
        this.connectionDetails = connectionDetails;
    }

    void validate(String path, List<ValidatorError> errors) {
        Rules.maxLength(path + ".description", description, 1050, errors);
        Rules.required(path + ".type", type, errors);
        if (connectionDetails == null || connectionDetails.isNull()) {
            Rules.required(path + ".connectionDetails", null, errors);
        }
    }
}
