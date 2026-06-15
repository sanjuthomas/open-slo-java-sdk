package com.sanjuthomas.openslosdk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sanjuthomas.openslo.Version;

final class EncodingMixins {
    private EncodingMixins() {}

    abstract static class ApiVersion {
        @JsonProperty("apiVersion")
        abstract Version getVersion();
    }

    abstract static class NamedObject extends ApiVersion {
        @JsonIgnore
        abstract String getName();

        @JsonIgnore
        abstract String displayName();
    }

    abstract static class Slo extends NamedObject {
        @JsonIgnore
        abstract boolean isComposite();
    }
}
