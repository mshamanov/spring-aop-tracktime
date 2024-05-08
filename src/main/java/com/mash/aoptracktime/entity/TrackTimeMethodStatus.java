package com.mash.aoptracktime.entity;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;
import com.mash.aoptracktime.rest.controller.TrackTimeRestController;

/**
 * Enum to indicate the result of a method call, used as a part of {@link TrackTimeStat}.
 *
 * @author Mikhail Shamanov
 * @see TrackTimeRestController
 */
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