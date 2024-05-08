package com.mash.aoptracktime.rest.mapper;

import com.mash.aoptracktime.entity.TrackTimeStat;
import com.mash.aoptracktime.rest.model.TrackTimeDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Mapper from TrackTimeStat {@link TrackTimeStat} to {@link TrackTimeDto}.
 *
 * @author Mikhail Shamanov
 * @see TrackTimeStat
 * @see TrackTimeDto
 */
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
                .returnType(stat.getReturnType())
                .packageName(stat.getPackageName())
                .className(stat.getClassName())
                .methodName(stat.getMethodName())
                .parameters(stat.getParameters())
                .executionTime(stat.getExecutionTime())
                .status(stat.getStatus())
                .createdAt(stat.getCreatedAt())
                .build();
    }
}
