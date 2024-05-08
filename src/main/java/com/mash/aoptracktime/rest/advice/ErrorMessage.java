package com.mash.aoptracktime.rest.advice;

import java.time.LocalDateTime;

/**
 * Record class to represent a simple error message for the rest controller.
 *
 * @author Mikhail Shamanov
 */
public record ErrorMessage(int statusCode, LocalDateTime timestamp, String message, String description) {
}