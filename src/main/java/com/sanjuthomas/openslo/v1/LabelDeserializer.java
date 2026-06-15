package com.sanjuthomas.openslo.v1;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LabelDeserializer extends JsonDeserializer<Label> {
    @Override
    public Label deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        Label label = new Label();
        if (node.isArray()) {
            for (JsonNode item : node) {
                label.add(item.asText());
            }
        } else if (node.isTextual()) {
            label.add(node.asText());
        }
        return label;
    }
}
