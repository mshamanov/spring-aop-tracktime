package com.mash.aoptracktime.rest.model;

/**
 * Record class to keep summary statistics calculated after processing a set of method execution time measurements.
 *
 * @author Mikhail Shamanov
 * @see TrackTimeDto
 */
public record TrackTimeSummary(long count, long min, long max, double average) {
}
