package com.sanjuthomas.openslosdk;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanjuthomas.openslo.Kind;
import com.sanjuthomas.openslo.OpenSloObject;
import com.sanjuthomas.openslo.Version;
import com.sanjuthomas.openslo.v1.AlertCondition;
import com.sanjuthomas.openslo.v1.AlertNotificationTarget;
import com.sanjuthomas.openslo.v1.AlertPolicy;
import com.sanjuthomas.openslo.v1.DataSource;
import com.sanjuthomas.openslo.v1.SLI;
import com.sanjuthomas.openslo.v1.SLO;
import com.sanjuthomas.openslo.v1.Service;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

final class ObjectDecoder {
    private static final Pattern JSON_ARRAY_IDENT = Pattern.compile("^\\s*\\[");
    private static final String YAML_DOC_SEP = "\n---";

    private ObjectDecoder() {}

    static List<OpenSloObject> decode(InputStream input, ObjectFormat format) throws IOException {
        return decode(input.readAllBytes(), format);
    }

    static List<OpenSloObject> decode(Reader reader, ObjectFormat format) throws IOException {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int read;
        while ((read = reader.read(buffer)) != -1) {
            builder.append(buffer, 0, read);
        }
        return decode(builder.toString().getBytes(StandardCharsets.UTF_8), format);
    }

    static List<OpenSloObject> decode(String data, ObjectFormat format) throws IOException {
        return decode(data.getBytes(StandardCharsets.UTF_8), format);
    }

    private static List<OpenSloObject> decode(byte[] data, ObjectFormat format) throws IOException {
        format.validate();
        return switch (format) {
            case YAML -> decodeYaml(data);
            case JSON -> decodeJson(data);
        };
    }

    private static List<OpenSloObject> decodeYaml(byte[] data) throws IOException {
        ObjectMapper mapper = JacksonConfig.decodeYamlMapper();
        List<JsonNode> documentNodes = new ArrayList<>();
        int offset = 0;
        while (offset <= data.length) {
            int separatorIndex = indexOf(data, YAML_DOC_SEP.getBytes(StandardCharsets.UTF_8), offset);
            byte[] document;
            if (separatorIndex >= 0) {
                document = slice(data, offset, separatorIndex);
                offset = skipSeparator(data, separatorIndex);
            } else {
                document = slice(data, offset, data.length);
                offset = data.length + 1;
            }
            if (document.length == 0 || isBlank(document)) {
                if (separatorIndex < 0) {
                    break;
                }
                continue;
            }
            documentNodes.addAll(parseYamlDocument(mapper, document));
            if (separatorIndex < 0) {
                break;
            }
        }
        return decodeNodes(mapper, documentNodes);
    }

    private static List<JsonNode> parseYamlDocument(ObjectMapper mapper, byte[] document) throws IOException {
        List<JsonNode> nodes = new ArrayList<>();
        if (isYamlArray(document)) {
            JsonNode root = mapper.readTree(document);
            if (root.isArray()) {
                root.forEach(nodes::add);
            }
        } else {
            nodes.add(mapper.readTree(document));
        }
        return nodes;
    }

    private static List<OpenSloObject> decodeJson(byte[] data) throws IOException {
        ObjectMapper mapper = JacksonConfig.decodeJsonMapper();
        List<JsonNode> nodes = new ArrayList<>();
        if (JSON_ARRAY_IDENT.matcher(new String(data, StandardCharsets.UTF_8)).find()) {
            JsonNode root = mapper.readTree(data);
            if (root.isArray()) {
                root.forEach(nodes::add);
            }
        } else {
            nodes.add(mapper.readTree(data));
        }
        return decodeNodes(mapper, nodes);
    }

    private static List<OpenSloObject> decodeNodes(ObjectMapper mapper, List<JsonNode> nodes) throws IOException {
        List<OpenSloObject> objects = new ArrayList<>(nodes.size());
        for (JsonNode node : nodes) {
            if (node == null || node.isNull() || (node.isObject() && node.isEmpty())) {
                continue;
            }
            Version version = Version.fromValue(requiredText(node, "apiVersion"));
            Kind kind = Kind.fromValue(requiredText(node, "kind"));
            objects.add(decodeObject(mapper, node, version, kind));
        }
        return objects;
    }

    private static OpenSloObject decodeObject(ObjectMapper mapper, JsonNode node, Version version, Kind kind)
            throws IOException {
        Class<? extends OpenSloObject> type = resolveType(version, kind);
        return mapper.treeToValue(node, type);
    }

    private static Class<? extends OpenSloObject> resolveType(Version version, Kind kind) {
        return switch (version) {
            case V1ALPHA -> switch (kind) {
                case SERVICE -> com.sanjuthomas.openslo.v1alpha.Service.class;
                case SLO -> com.sanjuthomas.openslo.v1alpha.SLO.class;
                default -> throw unsupportedKind(version, kind);
            };
            case V1 -> switch (kind) {
                case SERVICE -> Service.class;
                case SLO -> SLO.class;
                case SLI -> SLI.class;
                case DATA_SOURCE -> DataSource.class;
                case ALERT_POLICY -> AlertPolicy.class;
                case ALERT_CONDITION -> AlertCondition.class;
                case ALERT_NOTIFICATION_TARGET -> AlertNotificationTarget.class;
            };
            case V2ALPHA -> switch (kind) {
                case SERVICE -> com.sanjuthomas.openslo.v2alpha.Service.class;
                case SLO -> com.sanjuthomas.openslo.v2alpha.SLO.class;
                case SLI -> com.sanjuthomas.openslo.v2alpha.SLI.class;
                case DATA_SOURCE -> com.sanjuthomas.openslo.v2alpha.DataSource.class;
                case ALERT_POLICY -> com.sanjuthomas.openslo.v2alpha.AlertPolicy.class;
                case ALERT_CONDITION -> com.sanjuthomas.openslo.v2alpha.AlertCondition.class;
                case ALERT_NOTIFICATION_TARGET -> com.sanjuthomas.openslo.v2alpha.AlertNotificationTarget.class;
            };
        };
    }

    private static IllegalArgumentException unsupportedKind(Version version, Kind kind) {
        return new IllegalArgumentException(
                "unsupported " + kind.getClass().getSimpleName() + ": " + kind + " for version: " + version);
    }

    private static String requiredText(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            throw new IllegalArgumentException("'" + field + "' is required");
        }
        return value.asText();
    }

    private static boolean isYamlArray(byte[] document) throws IOException {
        try (BufferedReader reader = new BufferedReader(new StringReader(new String(document, StandardCharsets.UTF_8)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (line.startsWith("#")) {
                    continue;
                }
                if (line.startsWith("[")) {
                    return true;
                }
                if (line.startsWith("-")) {
                    return !"---".equals(line);
                }
                return false;
            }
        }
        return false;
    }

    private static int indexOf(byte[] data, byte[] pattern, int fromIndex) {
        if (pattern.length == 0 || fromIndex >= data.length) {
            return -1;
        }
        outer:
        for (int i = fromIndex; i <= data.length - pattern.length; i++) {
            for (int j = 0; j < pattern.length; j++) {
                if (data[i + j] != pattern[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    private static byte[] slice(byte[] data, int start, int end) {
        int length = Math.max(0, end - start);
        byte[] slice = new byte[length];
        System.arraycopy(data, start, slice, 0, length);
        return slice;
    }

    private static int skipSeparator(byte[] data, int separatorIndex) {
        int offset = separatorIndex + YAML_DOC_SEP.length();
        while (offset < data.length && (data[offset] == '\n' || data[offset] == '\r')) {
            offset++;
        }
        return offset;
    }

    private static boolean isBlank(byte[] data) {
        for (byte value : data) {
            if (!Character.isWhitespace(value)) {
                return false;
            }
        }
        return true;
    }
}
