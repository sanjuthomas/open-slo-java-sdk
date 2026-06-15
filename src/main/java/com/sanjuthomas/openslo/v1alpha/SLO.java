package com.sanjuthomas.openslo.v1alpha;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sanjuthomas.openslo.Kind;
import com.sanjuthomas.openslo.Version;
import com.sanjuthomas.openslo.internal.ObjectNames;
import com.sanjuthomas.openslo.validation.Rules;
import com.sanjuthomas.openslo.validation.ValidationException;
import com.sanjuthomas.openslo.validation.ValidatorError;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
public class SLO implements V1alphaObject {
    private Version apiVersion = Version.V1ALPHA;
    private Kind kind = Kind.SLO;
    private Metadata metadata;
    private SLOSpec spec = new SLOSpec();

    public SLO() {}

    public SLO(Metadata metadata, SLOSpec spec) {
        this.metadata = metadata;
        this.spec = spec;
    }

    @Override
    public Version getVersion() {
        return apiVersion;
    }

    public void setApiVersion(Version apiVersion) {
        this.apiVersion = apiVersion;
    }

    @Override
    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    @Override
    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public SLOSpec getSpec() {
        return spec;
    }

    public void setSpec(SLOSpec spec) {
        this.spec = spec;
    }

    @Override
    public String getName() {
        return metadata != null ? metadata.getName() : null;
    }

    @Override
    public String displayName() {
        return metadata != null && metadata.getDisplayName() != null
                ? metadata.getDisplayName()
                : getName();
    }

    @Override
    public void validate() throws ValidationException {
        List<ValidatorError> errors = new ArrayList<>();
        if (metadata != null) {
            metadata.validate("metadata", errors);
        }
        Rules.throwIfErrors(errors);
    }

    @Override
    public String toString() {
        return ObjectNames.getObjectName(this);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SLOSpec {
        private List<SLOTimeWindow> timeWindows = new ArrayList<>();
        private String budgetingMethod;
        private String description;
        private SLOIndicator indicator;
        private String service;
        private List<SLOObjective> objectives = new ArrayList<>();

        public List<SLOTimeWindow> getTimeWindows() {
            return timeWindows;
        }

        public void setTimeWindows(List<SLOTimeWindow> timeWindows) {
            this.timeWindows = timeWindows;
        }

        public String getBudgetingMethod() {
            return budgetingMethod;
        }

        public void setBudgetingMethod(String budgetingMethod) {
            this.budgetingMethod = budgetingMethod;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public SLOIndicator getIndicator() {
            return indicator;
        }

        public void setIndicator(SLOIndicator indicator) {
            this.indicator = indicator;
        }

        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }

        public List<SLOObjective> getObjectives() {
            return objectives;
        }

        public void setObjectives(List<SLOObjective> objectives) {
            this.objectives = objectives;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SLOIndicator {
        private SLOMetricSourceSpec thresholdMetric;

        public SLOMetricSourceSpec getThresholdMetric() {
            return thresholdMetric;
        }

        public void setThresholdMetric(SLOMetricSourceSpec thresholdMetric) {
            this.thresholdMetric = thresholdMetric;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SLOMetricSourceSpec {
        private String source;
        private String queryType;
        private String query;

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getQueryType() {
            return queryType;
        }

        public void setQueryType(String queryType) {
            this.queryType = queryType;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SLOObjective {
        private String displayName;
        private Double value;
        private SLORatioMetrics ratioMetrics;
        private Double target;
        private Double timeSliceTarget;
        private Operator op;

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }

        public SLORatioMetrics getRatioMetrics() {
            return ratioMetrics;
        }

        public void setRatioMetrics(SLORatioMetrics ratioMetrics) {
            this.ratioMetrics = ratioMetrics;
        }

        public Double getTarget() {
            return target;
        }

        public void setTarget(Double target) {
            this.target = target;
        }

        public Double getTimeSliceTarget() {
            return timeSliceTarget;
        }

        public void setTimeSliceTarget(Double timeSliceTarget) {
            this.timeSliceTarget = timeSliceTarget;
        }

        public Operator getOp() {
            return op;
        }

        public void setOp(Operator op) {
            this.op = op;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SLORatioMetrics {
        private SLOMetricSourceSpec good;
        private SLOMetricSourceSpec total;
        private boolean incremental;

        public SLOMetricSourceSpec getGood() {
            return good;
        }

        public void setGood(SLOMetricSourceSpec good) {
            this.good = good;
        }

        public SLOMetricSourceSpec getTotal() {
            return total;
        }

        public void setTotal(SLOMetricSourceSpec total) {
            this.total = total;
        }

        public boolean isIncremental() {
            return incremental;
        }

        public void setIncremental(boolean incremental) {
            this.incremental = incremental;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SLOTimeWindow {
        private String unit;
        private int count;
        @com.fasterxml.jackson.annotation.JsonProperty("isRolling")
        private boolean isRolling;
        private SLOCalendar calendar;

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public boolean isRolling() {
            return isRolling;
        }

        public void setRolling(boolean rolling) {
            isRolling = rolling;
        }

        public SLOCalendar getCalendar() {
            return calendar;
        }

        public void setCalendar(SLOCalendar calendar) {
            this.calendar = calendar;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SLOCalendar {
        private String startTime;
        private String timeZone;

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getTimeZone() {
            return timeZone;
        }

        public void setTimeZone(String timeZone) {
            this.timeZone = timeZone;
        }
    }
}
