package com.mash.aoptracktime.rest.controller;

import com.mash.aoptracktime.entity.TrackTimeStat;
import com.mash.aoptracktime.rest.mapper.TrackTimeDtoToSpecificationMapper;
import com.mash.aoptracktime.rest.mapper.TrackTimeStatToDtoMapper;
import com.mash.aoptracktime.rest.model.TrackTimeDto;
import com.mash.aoptracktime.service.TrackTimeStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;

@RestController
@RequestMapping("/api/tracktime")
@RequiredArgsConstructor
public class TrackTimeRestController {
    private final TrackTimeStatsService trackTimeStatsService;
    private final TrackTimeDtoToSpecificationMapper toSpecificationMapper;
    private final TrackTimeStatToDtoMapper toDtoMapper;

    @GetMapping(path = "/stats",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getTrackTimeStats(@RequestBody(required = false) TrackTimeDto request,
                                               @RequestParam(defaultValue = "true") boolean includeData,
                                               @RequestParam(defaultValue = "false") boolean methodNamesOnly) {
        List<TrackTimeStat> trackTimeStats;

        if (request == null) {
            trackTimeStats = this.trackTimeStatsService.findAll();
        } else {
            trackTimeStats = this.trackTimeStatsService.findAll(this.toSpecificationMapper.apply(request));
        }

        List<TrackTimeDto> resultList;

        if (methodNamesOnly) {
            resultList = trackTimeStats.stream()
                    .map(stat -> TrackTimeDto.builder()
                            .methodName(stat.getMethodName())
                            .executionTime(stat.getExecutionTime()).build())
                    .toList();
        } else {
            resultList = trackTimeStats.stream().map(this.toDtoMapper).toList();
        }

        LongSummaryStatistics summaryStatistics = resultList.stream()
                .mapToLong(TrackTimeDto::getExecutionTime)
                .summaryStatistics();

        Map<String, Object> result = new HashMap<>();

        if (includeData) {
            result.put("result", resultList);
        }

        if (!resultList.isEmpty()) {
            result.put("summary", summaryStatistics);
        }

        return ResponseEntity.ok(result);
    }
}
