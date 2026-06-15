package com.sanjuthomas.openslosdk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sanjuthomas.openslo.v1.AlertCondition;
import com.sanjuthomas.openslo.v1.AlertNotificationTarget;
import com.sanjuthomas.openslo.v1.AlertPolicy;
import com.sanjuthomas.openslo.v1.DataSource;
import com.sanjuthomas.openslo.v1.SLI;
import com.sanjuthomas.openslo.v1.SLO;
import com.sanjuthomas.openslo.v1.Service;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class JacksonConfig {
    private static final ObjectMapper DECODE_JSON_MAPPER = createDecodeJsonMapper();
    private static final ObjectMapper ENCODE_JSON_MAPPER = createEncodeJsonMapper();
    private static final ObjectMapper DECODE_YAML_MAPPER = createDecodeYamlMapper();
    private static final ObjectMapper ENCODE_YAML_MAPPER = createEncodeYamlMapper();

    private JacksonConfig() {}

    public static ObjectMapper decodeJsonMapper() {
        return DECODE_JSON_MAPPER;
    }

    public static ObjectMapper encodeJsonMapper() {
        return ENCODE_JSON_MAPPER;
    }

    public static ObjectMapper decodeYamlMapper() {
        return DECODE_YAML_MAPPER;
    }

    public static ObjectMapper encodeYamlMapper() {
        return ENCODE_YAML_MAPPER;
    }

    private static ObjectMapper createDecodeJsonMapper() {
        ObjectMapper mapper = new ObjectMapper();
        configureCommon(mapper);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        return mapper;
    }

    private static ObjectMapper createEncodeJsonMapper() {
        ObjectMapper mapper = new ObjectMapper();
        configureCommon(mapper);
        registerApiVersionMixins(mapper);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        return mapper;
    }

    private static ObjectMapper createDecodeYamlMapper() {
        YAMLFactory factory = YAMLFactory.builder()
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
                .build();
        ObjectMapper mapper = new ObjectMapper(factory);
        configureCommon(mapper);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        return mapper;
    }

    private static ObjectMapper createEncodeYamlMapper() {
        YAMLFactory factory = YAMLFactory.builder()
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
                .build();
        ObjectMapper mapper = new ObjectMapper(factory);
        configureCommon(mapper);
        registerApiVersionMixins(mapper);
        return mapper;
    }

    private static void registerApiVersionMixins(ObjectMapper mapper) {
        mapper.addMixIn(Service.class, EncodingMixins.NamedObject.class);
        mapper.addMixIn(SLO.class, EncodingMixins.Slo.class);
        mapper.addMixIn(SLI.class, EncodingMixins.NamedObject.class);
        mapper.addMixIn(DataSource.class, EncodingMixins.NamedObject.class);
        mapper.addMixIn(AlertPolicy.class, EncodingMixins.NamedObject.class);
        mapper.addMixIn(AlertCondition.class, EncodingMixins.NamedObject.class);
        mapper.addMixIn(AlertNotificationTarget.class, EncodingMixins.NamedObject.class);
        mapper.addMixIn(com.sanjuthomas.openslo.v1alpha.Service.class, EncodingMixins.NamedObject.class);
        mapper.addMixIn(com.sanjuthomas.openslo.v1alpha.SLO.class, EncodingMixins.NamedObject.class);
        mapper.addMixIn(com.sanjuthomas.openslo.v2alpha.Service.class, EncodingMixins.NamedObject.class);
        mapper.addMixIn(com.sanjuthomas.openslo.v2alpha.SLO.class, EncodingMixins.NamedObject.class);
        mapper.addMixIn(com.sanjuthomas.openslo.v2alpha.SLI.class, EncodingMixins.NamedObject.class);
        mapper.addMixIn(com.sanjuthomas.openslo.v2alpha.DataSource.class, EncodingMixins.NamedObject.class);
        mapper.addMixIn(com.sanjuthomas.openslo.v2alpha.AlertPolicy.class, EncodingMixins.NamedObject.class);
        mapper.addMixIn(com.sanjuthomas.openslo.v2alpha.AlertCondition.class, EncodingMixins.NamedObject.class);
        mapper.addMixIn(com.sanjuthomas.openslo.v2alpha.AlertNotificationTarget.class, EncodingMixins.NamedObject.class);
    }

    private static void configureCommon(ObjectMapper mapper) {
        mapper.registerModule(new JavaTimeModule());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
