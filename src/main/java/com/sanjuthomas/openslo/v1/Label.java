package com.sanjuthomas.openslo.v1;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(using = LabelDeserializer.class)
@JsonSerialize(using = LabelSerializer.class)
public class Label extends ArrayList<String> {
    public Label() {
        super();
    }

    public Label(List<String> values) {
        super(values);
    }
}
