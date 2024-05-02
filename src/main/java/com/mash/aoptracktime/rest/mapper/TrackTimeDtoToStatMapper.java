package com.mash.aoptracktime.rest.mapper;

import com.mash.aoptracktime.entity.TrackTimeStat;
import com.mash.aoptracktime.rest.model.TrackTimeDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class TrackTimeDtoToStatMapper implements Function<TrackTimeDto, TrackTimeStat> {
    @Override
    public TrackTimeStat apply(TrackTimeDto requestDto) {
        return TrackTimeStat.builder()
                .groupName(requestDto.getGroupName())
                .returnType(requestDto.getReturnType())
                .packageName(requestDto.getPackageName())
                .methodName(requestDto.getMethodName())
                .parameters(requestDto.getParameters())
                .status(requestDto.getStatus())
                .createdAt(requestDto.getCreatedAt())
                .build();
    }
}
