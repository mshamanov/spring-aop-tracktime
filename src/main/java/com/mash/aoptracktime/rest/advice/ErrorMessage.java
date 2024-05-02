package com.mash.aoptracktime.rest.advice;

import java.time.LocalDateTime;

public record ErrorMessage(int statusCode, LocalDateTime timestamp, String message, String description) {
}