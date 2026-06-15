package com.sanjuthomas.openslosdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.sanjuthomas.openslo.OpenSloObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

final class ObjectEncoder {
    private ObjectEncoder() {}

    static void encode(OutputStream output, ObjectFormat format, List<OpenSloObject> objects) throws IOException {
        format.validate();
        ObjectWriter writer = writerFor(format);
        writer.writeValue(output, objects);
    }

    static void encode(Writer output, ObjectFormat format, List<OpenSloObject> objects) throws IOException {
        format.validate();
        ObjectWriter writer = writerFor(format);
        writer.writeValue(output, objects);
    }

    static void encode(OutputStream output, ObjectFormat format, OpenSloObject... objects) throws IOException {
        encode(output, format, Arrays.asList(objects));
    }

    static void encode(Writer output, ObjectFormat format, OpenSloObject... objects) throws IOException {
        encode(output, format, Arrays.asList(objects));
    }

    private static ObjectWriter writerFor(ObjectFormat format) {
        ObjectMapper mapper = switch (format) {
            case YAML -> JacksonConfig.encodeYamlMapper();
            case JSON -> JacksonConfig.encodeJsonMapper();
        };
        return mapper.writer();
    }
}
