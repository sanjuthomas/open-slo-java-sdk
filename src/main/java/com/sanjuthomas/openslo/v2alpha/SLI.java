package com.sanjuthomas.openslo.v2alpha;

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
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
public class SLI implements V2alphaObject {
    private Version apiVersion = Version.V2ALPHA;
    private Kind kind = Kind.SLI;
    private Metadata metadata;
    private SLISpec spec = new SLISpec();

    public SLI() {}

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

    public SLISpec getSpec() {
        return spec;
    }

    public void setSpec(SLISpec spec) {
        this.spec = spec;
    }

    @Override
    public String getName() {
        return metadata != null ? metadata.getName() : null;
    }

    @Override
    public String displayName() {
        return getName();
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
    public static class SLISpec {
        private String description;
        private SLIMetricSpec thresholdMetric;
        private SLIRatioMetric ratioMetric;

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
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SLIRatioMetric {
        private boolean counter;
        private SLIMetricSpec good;
        private SLIMetricSpec bad;
        private SLIMetricSpec total;
        private String rawType;
        private SLIMetricSpec raw;

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

        public String getRawType() {
            return rawType;
        }

        public void setRawType(String rawType) {
            this.rawType = rawType;
        }

        public SLIMetricSpec getRaw() {
            return raw;
        }

        public void setRaw(SLIMetricSpec raw) {
            this.raw = raw;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SLIMetricSpec {
        private String dataSourceRef;
        private DataSource.DataSourceSpec dataSourceSpec;
        private Map<String, Object> spec;

        public String getDataSourceRef() {
            return dataSourceRef;
        }

        public void setDataSourceRef(String dataSourceRef) {
            this.dataSourceRef = dataSourceRef;
        }

        public DataSource.DataSourceSpec getDataSourceSpec() {
            return dataSourceSpec;
        }

        public void setDataSourceSpec(DataSource.DataSourceSpec dataSourceSpec) {
            this.dataSourceSpec = dataSourceSpec;
        }

        public Map<String, Object> getSpec() {
            return spec;
        }

        public void setSpec(Map<String, Object> spec) {
            this.spec = spec;
        }
    }
}
