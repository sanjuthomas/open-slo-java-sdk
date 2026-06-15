package com.sanjuthomas.openslo.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sanjuthomas.openslo.validation.Rules;
import com.sanjuthomas.openslo.validation.ValidatorError;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
public class SLIRatioMetric {
    private boolean counter;
    private SLIMetricSpec good;
    private SLIMetricSpec bad;
    private SLIMetricSpec total;
    private SLIRawMetricType rawType;
    private SLIMetricSpec raw;

    public SLIRatioMetric() {}

    public boolean isCounter() {
        return counter;
    }

    public void setCounter(boolean counter) {
        this.counter = counter;
    }

    public SLIMetricSpec getGood() {
        return good;
    }

    public void setGood(SLIMetricSpec good) {
        this.good = good;
    }

    public SLIMetricSpec getBad() {
        return bad;
    }

    public void setBad(SLIMetricSpec bad) {
        this.bad = bad;
    }

    public SLIMetricSpec getTotal() {
        return total;
    }

    public void setTotal(SLIMetricSpec total) {
        this.total = total;
    }

    public SLIRawMetricType getRawType() {
        return rawType;
    }

    public void setRawType(SLIRawMetricType rawType) {
        this.rawType = rawType;
    }

    public SLIMetricSpec getRaw() {
        return raw;
    }

    public void setRaw(SLIMetricSpec raw) {
        this.raw = raw;
    }

    void validate(String path, List<ValidatorError> errors) {
        Rules.mutuallyExclusive(
                path,
                true,
                Rules.fieldsMap("total", total, "raw", raw),
                errors);
        Rules.mutuallyExclusive(
                path,
                false,
                Rules.fieldsMap("raw", raw, "good", good),
                errors);
        Rules.mutuallyExclusive(
                path,
                false,
                Rules.fieldsMap("raw", raw, "bad", bad),
                errors);

        if (total != null) {
            Rules.oneOfProperties(path, Rules.fieldsMap("good", good, "bad", bad), errors);
            total.validate(path + ".total", errors);
            if (good != null) {
                good.validate(path + ".good", errors);
            }
            if (bad != null) {
                bad.validate(path + ".bad", errors);
            }
        }

        if (raw != null) {
            raw.validate(path + ".raw", errors);
            Rules.required(path + ".rawType", rawType, errors);
            if (rawType != null) {
                rawType.validate(path + ".rawType", errors);
            }
        }
    }
}
