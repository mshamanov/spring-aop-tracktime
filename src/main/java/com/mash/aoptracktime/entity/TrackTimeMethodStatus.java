package com.mash.aoptracktime.entity;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TrackTimeMethodStatus {
    @JsonEnumDefaultValue
    COMPLETED("completed"),
    EXCEPTION("exception");

    private final String value;

    TrackTimeMethodStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }
}