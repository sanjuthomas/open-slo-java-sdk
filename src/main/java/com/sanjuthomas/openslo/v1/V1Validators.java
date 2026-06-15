package com.sanjuthomas.openslo.v1;

import com.sanjuthomas.openslo.Kind;
import com.sanjuthomas.openslo.Version;
import com.sanjuthomas.openslo.validation.Rules;
import com.sanjuthomas.openslo.validation.ValidatorError;

import java.util.List;

public final class V1Validators {
    public static final Version API_VERSION = Version.V1;

    private V1Validators() {}

    public static void validateApiVersion(String path, Version apiVersion, List<ValidatorError> errors) {
        Rules.required(path + ".apiVersion", apiVersion, errors);
        Rules.eq(path + ".apiVersion", apiVersion, API_VERSION, errors);
    }

    public static void validateKind(String path, Kind kind, Kind expected, List<ValidatorError> errors) {
        Rules.required(path + ".kind", kind, errors);
        Rules.eq(path + ".kind", kind, expected, errors);
    }

    public static void validateMetadata(String path, Metadata metadata, List<ValidatorError> errors) {
        Rules.required(path + ".metadata", metadata, errors);
        if (metadata != null) {
            metadata.validate(path + ".metadata", errors);
        }
    }

    public static void validateSloIndicator(
            String path,
            SLOIndicatorInline indicator,
            String indicatorRef,
            List<ValidatorError> errors) {
        Rules.mutuallyExclusive(
                path,
                true,
                Rules.fieldsMap("indicator", indicator, "indicatorRef", indicatorRef),
                errors);

        if (indicator != null) {
            indicator.validate(path + ".indicator", errors);
        }
        if (indicatorRef != null && !indicatorRef.isEmpty()) {
            Rules.dnsLabel(path + ".indicatorRef", indicatorRef, errors);
        }
    }

    public static void validateSloIndicatorRule(SLOSpec spec, String path, List<ValidatorError> errors) {
        boolean hasComposites = spec.hasCompositeObjectives();
        boolean hasIndicator = spec.getIndicator() != null
                || (spec.getIndicatorRef() != null && !spec.getIndicatorRef().isEmpty());
        String msg = "'indicator' or 'indicatorRef' fields must either be defined on the 'spec' level "
                + "(standard SLOs) or on the 'spec.objectives[*]' level (composite SLOs)";
        if (!hasComposites && !hasIndicator) {
            errors.add(new ValidatorError(msg + ", but none were provided").withPropertyPath(path));
        }
        if (hasComposites && hasIndicator) {
            errors.add(new ValidatorError(msg + ", but not both").withPropertyPath(path));
        }
    }

    public static void validateTimeSliceWindow(String path, DurationShorthand timeSliceWindow, List<ValidatorError> errors) {
        Rules.required(path, timeSliceWindow, errors);
        if (timeSliceWindow != null) {
            timeSliceWindow.validate(path, errors);
        }
    }
}
