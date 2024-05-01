package com.mash.aoptracktime.rest.controller;

import com.mash.aoptracktime.entity.TrackTimeStat;
import com.mash.aoptracktime.rest.mapper.TrackTimeStatToDtoMapper;
import com.mash.aoptracktime.rest.mapper.TrackTimeDtoToSpecificationMapper;
import com.mash.aoptracktime.rest.model.TrackTimeDto;
import com.mash.aoptracktime.service.TrackTimeStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;

@RestController
@RequestMapping("/api/tracktimestats")
@RequiredArgsConstructor
public class TrackTimeRestController {
    private final TrackTimeStatsService trackTimeStatsService;
    private final TrackTimeDtoToSpecificationMapper toSpecificationMapper;
    private final TrackTimeStatToDtoMapper toDtoMapper;

    @GetMapping(path = "/summary", consumes = "application/json")
    public ResponseEntity<?> getTrackTimeStats(@RequestBody TrackTimeDto request) {
        List<TrackTimeStat> trackTimeStats =
                this.trackTimeStatsService.findAll(this.toSpecificationMapper.apply(request));

        List<TrackTimeDto> dtoList = trackTimeStats.stream().map(this.toDtoMapper).toList();

        LongSummaryStatistics summaryStatistics = dtoList.stream()
                .mapToLong(TrackTimeDto::getExecutionTime)
                .summaryStatistics();

        Map<String, Object> result = new HashMap<>();

        result.put("result", dtoList);

        if (!dtoList.isEmpty()) {
            result.put("summary", summaryStatistics);
        }

        return ResponseEntity.ok(result);
    }
}
