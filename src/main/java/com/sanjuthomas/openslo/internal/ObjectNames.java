package com.sanjuthomas.openslo.internal;

import com.sanjuthomas.openslo.Kind;
import com.sanjuthomas.openslo.OpenSloObject;
import com.sanjuthomas.openslo.Version;

public final class ObjectNames {
    private ObjectNames() {}

    public static String getObjectName(OpenSloObject object) {
        Version version = object.getVersion();
        String versionStr = version.getValue();
        int slash = versionStr.indexOf('/');
        String shortVersion = slash == -1 ? "" : versionStr.substring(slash + 1);
        String name = object.getName();
        if (name != null && !name.isEmpty()) {
            return shortVersion + "." + object.getKind() + " '" + name + "'";
        }
        return shortVersion + "." + object.getKind();
    }
}
