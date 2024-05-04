package com.mash.aoptracktime.rest.controller;

import com.mash.aoptracktime.entity.TrackTimeStat;
import com.mash.aoptracktime.rest.mapper.LongStatisticsToTrackTimeSummary;
import com.mash.aoptracktime.rest.mapper.TrackTimeDtoToSpecificationMapper;
import com.mash.aoptracktime.rest.mapper.TrackTimeEntityToDtoMapper;
import com.mash.aoptracktime.rest.model.TrackTimeDto;
import com.mash.aoptracktime.service.TrackTimeStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/tracktimestats",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TrackTimeRestController {
    public static final class ViewTypes {
        public static final String ALL = "ALL";
        public static final String DATA = "DATA";
        public static final String SUMMARY = "SUMMARY";
    }

    private final TrackTimeStatsService trackTimeStatsService;

    private final TrackTimeDtoToSpecificationMapper toSpecificationMapper;
    private final TrackTimeEntityToDtoMapper toDtoMapper;
    private final LongStatisticsToTrackTimeSummary statisticsToSummaryMapper;

    @GetMapping({"", "/", "/all"})
    public ResponseEntity<?> getTrackTimeStats(@RequestParam(name = "view", defaultValue = "all") String viewType,
                                               @RequestParam(name = "short", defaultValue = "false") boolean shortInfo) {
        List<TrackTimeStat> timeStats = this.trackTimeStatsService.findAll();
        return this.prepareResponse(timeStats, viewType, shortInfo);
    }

    @PostMapping("/search")
    public ResponseEntity<?> getTrackTimeStats(@RequestBody(required = false) TrackTimeDto requestDto,
                                               @RequestParam(name = "view", defaultValue = "all") String viewType,
                                               @RequestParam(name = "short", defaultValue = "false") boolean shortInfo) {
        if (requestDto == null || TrackTimeDto.isAllEmpty(requestDto)) {
            throw new IllegalStateException("At least one search property must be set");
        }

        List<TrackTimeStat> timeStats = this.trackTimeStatsService.findAll(this.toSpecificationMapper.apply(requestDto));
        return this.prepareResponse(timeStats, viewType, shortInfo);
    }

    private ResponseEntity<Map<String, Object>> prepareResponse(Collection<? extends TrackTimeStat> data,
                                                                String viewType, boolean shortInfo) {
        List<TrackTimeDto> resultList;

        if (shortInfo) {
            resultList = data.stream()
                    .map(stat -> TrackTimeDto.builder()
                            .methodName(stat.getMethodName())
                            .executionTime(stat.getExecutionTime()).build())
                    .toList();
        } else {
            resultList = data.stream().map(this.toDtoMapper).toList();
        }

        var statistics = resultList.stream().mapToLong(TrackTimeDto::getExecutionTime).summaryStatistics();
        var summary = this.statisticsToSummaryMapper.apply(statistics);

        Map<String, Object> body = new HashMap<>();

        if (viewType.isBlank() || viewType.equalsIgnoreCase(ViewTypes.ALL)) {
            body.put("result", resultList);
            body.put("summary", summary);
        } else if (viewType.equalsIgnoreCase(ViewTypes.DATA)) {
            body.put("result", resultList);
        } else if (viewType.equalsIgnoreCase(ViewTypes.SUMMARY)) {
            body.put("summary", summary);
        }

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);
    }
}
