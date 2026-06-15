package com.sanjuthomas.openslosdk;

import com.sanjuthomas.openslo.OpenSloObject;
import com.sanjuthomas.openslo.internal.ObjectNames;
import com.sanjuthomas.openslo.validation.ValidationException;
import com.sanjuthomas.openslo.validation.ValidatorError;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class OpenSloSdk {
    private OpenSloSdk() {}

    public static List<OpenSloObject> decode(InputStream input, ObjectFormat format) throws IOException {
        return ObjectDecoder.decode(input, format);
    }

    public static List<OpenSloObject> decode(Reader reader, ObjectFormat format) throws IOException {
        return ObjectDecoder.decode(reader, format);
    }

    public static List<OpenSloObject> decode(String data, ObjectFormat format) throws IOException {
        return ObjectDecoder.decode(data, format);
    }

    public static void encode(OutputStream output, ObjectFormat format, List<OpenSloObject> objects) throws IOException {
        ObjectEncoder.encode(output, format, objects);
    }

    public static void encode(Writer output, ObjectFormat format, List<OpenSloObject> objects) throws IOException {
        ObjectEncoder.encode(output, format, objects);
    }

    public static void encode(OutputStream output, ObjectFormat format, OpenSloObject... objects) throws IOException {
        ObjectEncoder.encode(output, format, objects);
    }

    public static void encode(Writer output, ObjectFormat format, OpenSloObject... objects) throws IOException {
        ObjectEncoder.encode(output, format, objects);
    }

    public static void validate(List<OpenSloObject> objects) throws ValidationException {
        validate(objects.toArray(OpenSloObject[]::new));
    }

    public static void validate(OpenSloObject... objects) throws ValidationException {
        List<ValidatorError> errors = new ArrayList<>();
        for (int i = 0; i < objects.length; i++) {
            OpenSloObject object = objects[i];
            try {
                object.validate();
            } catch (ValidationException ex) {
                String name = ObjectNames.getObjectName(object);
                for (ValidatorError error : ex.getErrors()) {
                    errors.add(error.withSliceIndex(i).withName(name));
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    public static <T extends OpenSloObject> List<T> filterByType(List<OpenSloObject> objects, Class<T> type) {
        List<T> filtered = new ArrayList<>();
        for (OpenSloObject object : objects) {
            if (type.isInstance(object)) {
                filtered.add(type.cast(object));
            }
        }
        return filtered;
    }
}
