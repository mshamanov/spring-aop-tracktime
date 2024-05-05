package com.mash.aoptracktime.rest.mapper;

import com.mash.aoptracktime.entity.TrackTimeStat;
import com.mash.aoptracktime.rest.model.TrackTimeDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class TrackTimeEntityToDtoMapper {
    public Function<TrackTimeStat, TrackTimeDto> toShort() {
        return stat -> TrackTimeDto.builder()
                .methodName(stat.getMethodName())
                .executionTime(stat.getExecutionTime()).build();
    }

    public Function<TrackTimeStat, TrackTimeDto> toNormal() {
        return stat -> TrackTimeDto.builder()
                .groupName(stat.getGroupName())
                .packageName(stat.getPackageName())
                .className(stat.getPackageName())
                .methodName(stat.getMethodName())
                .parameters(stat.getParameters())
                .returnType(stat.getReturnType())
                .executionTime(stat.getExecutionTime())
                .status(stat.getStatus())
                .createdAt(stat.getCreatedAt())
                .build();
    }
}
