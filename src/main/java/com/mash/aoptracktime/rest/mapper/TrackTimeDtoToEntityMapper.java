package com.mash.aoptracktime.rest.mapper;

import com.mash.aoptracktime.entity.TrackTimeStat;
import com.mash.aoptracktime.rest.model.TrackTimeDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Mapper from TrackTimeDto {@link TrackTimeDto} to {@link TrackTimeStat}.
 *
 * @author Mikhail Shamanov
 * @see TrackTimeDto
 * @see TrackTimeStat
 */
@Component
public class TrackTimeDtoToEntityMapper implements Function<TrackTimeDto, TrackTimeStat> {
    @Override
    public TrackTimeStat apply(TrackTimeDto requestDto) {
        return TrackTimeStat.builder()
                .groupName(requestDto.getGroupName())
                .returnType(requestDto.getReturnType())
                .packageName(requestDto.getPackageName())
                .className(requestDto.getClassName())
                .methodName(requestDto.getMethodName())
                .parameters(requestDto.getParameters())
                .status(requestDto.getStatus())
                .createdAt(requestDto.getCreatedAt())
                .build();
    }
}
