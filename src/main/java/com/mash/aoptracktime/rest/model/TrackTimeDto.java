package com.mash.aoptracktime.rest.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mash.aoptracktime.entity.TrackTimeMethodStatus;
import com.mash.aoptracktime.rest.converter.CustomLocalDateDeserializer;
import com.mash.aoptracktime.rest.converter.CustomLocalDateTimeDeserializer;
import com.mash.aoptracktime.rest.converter.CustomTrackTimeMethodStatusDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Stream;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrackTimeDto {
    @JsonProperty("groupName")
    private String groupName;

    @JsonProperty("returnType")
    private String returnType;

    @JsonProperty("packageName")
    private String packageName;

    @JsonProperty("className")
    private String className;

    @JsonProperty("methodName")
    private String methodName;

    @JsonProperty("parameters")
    private String parameters;

    @JsonProperty(value = "executionTime", access = JsonProperty.Access.READ_ONLY)
    private Long executionTime;

    @JsonProperty(value = "status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonDeserialize(using = CustomTrackTimeMethodStatusDeserializer.class)
    private TrackTimeMethodStatus status;

    @JsonProperty(value = "createdAt")
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonProperty(value = "startDate", access = JsonProperty.Access.WRITE_ONLY)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate startDate;

    @JsonProperty(value = "endDate", access = JsonProperty.Access.WRITE_ONLY)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate endDate;

    public static boolean isAllNull(TrackTimeDto trackTimeDto) {
        return Stream.of(trackTimeDto.groupName,
                        trackTimeDto.returnType,
                        trackTimeDto.packageName,
                        trackTimeDto.className,
                        trackTimeDto.methodName,
                        trackTimeDto.parameters,
                        trackTimeDto.status,
                        trackTimeDto.createdAt,
                        trackTimeDto.startDate,
                        trackTimeDto.endDate)
                .allMatch(Objects::isNull);
    }
}
