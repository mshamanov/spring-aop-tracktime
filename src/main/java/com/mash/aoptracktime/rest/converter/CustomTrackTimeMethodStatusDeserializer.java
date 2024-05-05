package com.mash.aoptracktime.rest.converter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.mash.aoptracktime.entity.TrackTimeMethodStatus;

import java.io.IOException;
import java.util.Arrays;

public class CustomTrackTimeMethodStatusDeserializer extends JsonDeserializer<TrackTimeMethodStatus> {
    public TrackTimeMethodStatus deserialize(JsonParser p, DeserializationContext context) throws IOException {
        String valueAsString = p.getValueAsString();
        try {
            return TrackTimeMethodStatus.valueOf(valueAsString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new JsonParseException(p, "Specified track time method status is invalid: " + valueAsString +
                                            ", valid values: " + Arrays.toString(TrackTimeMethodStatus.values()));
        }
    }
}
