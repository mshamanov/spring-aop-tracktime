package com.mash.aoptracktime.rest.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Custom deserializer from json data to an instance of LocalDateTime {@link LocalDateTime}
 * to use a pattern without the need to specify LocalTime {@link LocalTime} — it always sets to LocalTime.MIDNIGHT.
 *
 * @author Mikhail Shamanov
 */
public class CustomLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    private final CustomLocalDateDeserializer localDateDeserializer = new CustomLocalDateDeserializer();

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext context) throws IOException {
        return LocalDateTime.of(this.localDateDeserializer.deserialize(p, context), LocalTime.MIDNIGHT);
    }
}