package com.mash.aoptracktime.rest.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CustomLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext context) throws IOException {
        return LocalDateTime.of(LocalDate.parse(p.getValueAsString(), this.formatter), LocalTime.MIDNIGHT);
    }
}