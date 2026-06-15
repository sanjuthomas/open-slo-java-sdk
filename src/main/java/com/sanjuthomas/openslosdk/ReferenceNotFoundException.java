package com.sanjuthomas.openslosdk;

import com.sanjuthomas.openslo.Kind;
public final class ReferenceNotFoundException extends Exception {
    private final String objectName;
    private final String fieldPath;
    private final Kind expectedKind;

    public ReferenceNotFoundException(Kind expectedKind, String fieldPath, String objectName) {
        super(formatMessage(expectedKind, fieldPath, objectName));
        this.expectedKind = expectedKind;
        this.fieldPath = fieldPath;
        this.objectName = objectName;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getFieldPath() {
        return fieldPath;
    }

    public Kind getExpectedKind() {
        return expectedKind;
    }

    ReferenceNotFoundException withFieldPathPrefix(String prefix) {
        return new ReferenceNotFoundException(expectedKind, prefix + fieldPath, objectName);
    }

    private static String formatMessage(Kind expectedKind, String fieldPath, String objectName) {
        return "v1." + expectedKind + " '" + objectName + "' referenced at '" + fieldPath + "' does not exist";
    }
}
