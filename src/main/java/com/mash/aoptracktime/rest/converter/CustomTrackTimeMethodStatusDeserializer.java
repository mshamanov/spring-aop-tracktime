package com.mash.aoptracktime.rest.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.mash.aoptracktime.entity.TrackTimeMethodStatus;

import java.io.IOException;

public class CustomTrackTimeMethodStatusDeserializer extends JsonDeserializer<TrackTimeMethodStatus> {
    public TrackTimeMethodStatus deserialize(JsonParser p, DeserializationContext context) throws IOException {
        try {
            return TrackTimeMethodStatus.valueOf(p.getValueAsString().toUpperCase());
        } catch (IllegalArgumentException e) {
            return TrackTimeMethodStatus.COMPLETED;
        }
    }
}
