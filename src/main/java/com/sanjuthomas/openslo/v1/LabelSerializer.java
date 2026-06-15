package com.sanjuthomas.openslo.v1;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class LabelSerializer extends JsonSerializer<Label> {
    @Override
    public void serialize(Label value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray();
        for (String item : value) {
            gen.writeString(item);
        }
        gen.writeEndArray();
    }
}
