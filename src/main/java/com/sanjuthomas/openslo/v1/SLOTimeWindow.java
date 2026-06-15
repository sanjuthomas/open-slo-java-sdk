package com.sanjuthomas.openslo.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sanjuthomas.openslo.validation.ValidatorError;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
public class SLOTimeWindow {
    private DurationShorthand duration;
    private boolean isRolling;
    private SLOCalendar calendar;

    public SLOTimeWindow() {}

    public DurationShorthand getDuration() {
        return duration;
    }

    public void setDuration(DurationShorthand duration) {
        this.duration = duration;
    }

    @JsonProperty("isRolling")
    public boolean isRolling() {
        return isRolling;
    }

    @JsonProperty("isRolling")
    public void setIsRolling(boolean isRolling) {
        this.isRolling = isRolling;
    }

    public SLOCalendar getCalendar() {
        return calendar;
    }

    public void setCalendar(SLOCalendar calendar) {
        this.calendar = calendar;
    }

    void validate(String path, List<ValidatorError> errors) {
        if (isRolling && calendar != null) {
            errors.add(new ValidatorError("'calendar' cannot be set when 'isRolling' is true")
                    .withPropertyPath(path));
        }
        if (!isRolling && calendar == null) {
            errors.add(new ValidatorError("'calendar' must be set when 'isRolling' is false")
                    .withPropertyPath(path));
        }
        com.sanjuthomas.openslo.validation.Rules.required(path + ".duration", duration, errors);
        if (duration != null) {
            duration.validate(path + ".duration", errors);
        }
        if (calendar != null) {
            calendar.validate(path + ".calendar", errors);
        }
    }
}
