package com.mash.aoptracktime.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mash.aoptracktime.entity.TrackTimeMethodStatus;
import com.mash.aoptracktime.entity.TrackTimeStat;
import com.mash.aoptracktime.repository.TrackTimeStatsRepository;
import com.mash.aoptracktime.rest.mapper.LongStatisticsToSummaryMapper;
import com.mash.aoptracktime.rest.mapper.TrackTimeDtoToEntityMapper;
import com.mash.aoptracktime.rest.mapper.TrackTimeDtoToSpecificationMapper;
import com.mash.aoptracktime.rest.mapper.TrackTimeEntityToDtoMapper;
import com.mash.aoptracktime.rest.model.TrackTimeDto;
import com.mash.aoptracktime.service.TrackTimeStatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrackTimeRestController.class)
@ActiveProfiles("test")
class TrackTimeRestControllerMvcTest {
    @MockBean
    TrackTimeStatsRepository repository;

    @SpyBean
    TrackTimeStatsService service;

    @SpyBean
    TrackTimeDtoToEntityMapper trackTimeDtoToEntityMapper;

    @SpyBean
    TrackTimeDtoToSpecificationMapper toSpecificationMapper;

    @SpyBean
    TrackTimeEntityToDtoMapper toDtoMapper;

    @SpyBean
    LongStatisticsToSummaryMapper statisticsToSummaryMapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    List<TrackTimeStat> trackTimeStats;

    @BeforeEach
    void setUp() {
        this.trackTimeStats = List.of(
                TrackTimeStat.builder()
                        .id(1L)
                        .groupName("async")
                        .packageName("com.mash.aoptracktime.service")
                        .className("StudentsService")
                        .methodName("addStudent")
                        .executionTime(5023L)
                        .parameters("Object")
                        .status(TrackTimeMethodStatus.COMPLETED)
                        .createdAt(LocalDateTime.now().minusDays(2))
                        .build(),
                TrackTimeStat.builder()
                        .id(2L)
                        .groupName("sync")
                        .packageName("com.mash.aoptracktime.service")
                        .className("EmployeesService")
                        .methodName("addEmployee")
                        .executionTime(2015L)
                        .parameters("Object")
                        .status(TrackTimeMethodStatus.EXCEPTION)
                        .createdAt(LocalDateTime.now().minusDays(5))
                        .build(),
                TrackTimeStat.builder()
                        .id(3L)
                        .groupName("sync")
                        .packageName("com.mash.aoptracktime.service")
                        .className("EmployeesService")
                        .methodName("getEmployees")
                        .executionTime(1033L)
                        .parameters(null)
                        .status(TrackTimeMethodStatus.COMPLETED)
                        .createdAt(LocalDateTime.now().minusDays(5))
                        .build()
        );
    }

    @Test
    @DisplayName("GET /api/v1/tracktime/stats :: query params are default")
    void handleGetStats_whenQueryParamsAreDefault_returnsAllDataWithSummary() throws Exception {
        when(this.repository.findAll()).thenReturn(this.trackTimeStats);

        List<TrackTimeDto> dtoList = this.trackTimeStats.stream().map(this.toDtoMapper.toNormal()).toList();
        var statistics = dtoList.stream().mapToLong(TrackTimeDto::getExecutionTime).summaryStatistics();

        Map<String, Object> resultBody = Map.of(
                "result", dtoList,
                "summary", this.statisticsToSummaryMapper.apply(statistics)
        );
        String jsonContent = this.objectMapper.writeValueAsString(resultBody);

        this.mockMvc.perform(get("/api/v1/tracktime/stats").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(jsonContent, true)
                );

        verify(this.repository).findAll();
    }

    @Test
    @DisplayName("GET /api/v1/tracktime/stats?view=all")
    void handleGetStats_whenViewTypeIsAll_returnsAllDataWithSummary() throws Exception {
        when(this.repository.findAll()).thenReturn(this.trackTimeStats);

        List<TrackTimeDto> dtoList = this.trackTimeStats.stream().map(this.toDtoMapper.toNormal()).toList();
        var statistics = dtoList.stream().mapToLong(TrackTimeDto::getExecutionTime).summaryStatistics();

        Map<String, Object> resultBody = Map.of(
                "result", dtoList,
                "summary", this.statisticsToSummaryMapper.apply(statistics)
        );
        String jsonContent = this.objectMapper.writeValueAsString(resultBody);

        this.mockMvc.perform(get("/api/v1/tracktime/stats").contentType(MediaType.APPLICATION_JSON)
                        .queryParam("view", "all"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(jsonContent, true)
                );

        verify(this.repository).findAll();
    }

    @Test
    @DisplayName("GET /api/v1/tracktime/stats?view=data")
    void handleGetStats_whenViewTypeIsData_returnsOnlyDataWithNoSummary() throws Exception {
        when(this.repository.findAll()).thenReturn(this.trackTimeStats);

        List<TrackTimeDto> dtoList = this.trackTimeStats.stream().map(this.toDtoMapper.toNormal()).toList();

        Map<String, Object> resultBody = Map.of("result", dtoList);
        String jsonContent = this.objectMapper.writeValueAsString(resultBody);

        this.mockMvc.perform(get("/api/v1/tracktime/stats").contentType(MediaType.APPLICATION_JSON)
                        .queryParam("view", "data"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(jsonContent, true)
                );

        verify(this.repository).findAll();
    }

    @Test
    @DisplayName("GET /api/v1/tracktime/stats?view=summary")
    void handleGetStats_whenViewTypeIsSummary_returnsOnlySummaryWithNoData() throws Exception {
        when(this.repository.findAll()).thenReturn(this.trackTimeStats);

        List<TrackTimeDto> dtoList = this.trackTimeStats.stream().map(this.toDtoMapper.toNormal()).toList();
        var statistics = dtoList.stream().mapToLong(TrackTimeDto::getExecutionTime).summaryStatistics();

        Map<String, Object> resultBody = Map.of("summary", this.statisticsToSummaryMapper.apply(statistics));
        String jsonContent = this.objectMapper.writeValueAsString(resultBody);

        this.mockMvc.perform(get("/api/v1/tracktime/stats").contentType(MediaType.APPLICATION_JSON)
                        .queryParam("view", "summary"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(jsonContent, true)
                );

        verify(this.repository).findAll();
    }

    @Test
    @DisplayName("GET /api/v1/tracktime/stats?view=all&short=true")
    void handleGetStats_whenViewTypeIsAll_shortFormatIsTrue_returnsAllDataInShortFormatWithSummary() throws Exception {
        when(this.repository.findAll()).thenReturn(this.trackTimeStats);

        List<TrackTimeDto> dtoList = this.trackTimeStats.stream().map(this.toDtoMapper.toShort()).toList();
        var statistics = dtoList.stream().mapToLong(TrackTimeDto::getExecutionTime).summaryStatistics();

        Map<String, Object> resultBody = Map.of(
                "result", dtoList,
                "summary", this.statisticsToSummaryMapper.apply(statistics)
        );
        String jsonContent = this.objectMapper.writeValueAsString(resultBody);

        this.mockMvc.perform(get("/api/v1/tracktime/stats").contentType(MediaType.APPLICATION_JSON)
                        .queryParam("view", "all")
                        .queryParam("short", "true"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(jsonContent, true)
                );

        verify(this.repository).findAll();
    }

    @Test
    @DisplayName("GET /api/v1/tracktime/stats?view=data&short=true")
    void handleGetStats_whenViewTypeIsData_shortFormatIsTrue_returnsOnlyDataInShortFormatWithNoSummary() throws Exception {
        when(this.repository.findAll()).thenReturn(this.trackTimeStats);

        List<TrackTimeDto> dtoList = this.trackTimeStats.stream().map(this.toDtoMapper.toShort()).toList();

        Map<String, Object> resultBody = Map.of("result", dtoList);
        String jsonContent = this.objectMapper.writeValueAsString(resultBody);

        this.mockMvc.perform(get("/api/v1/tracktime/stats").contentType(MediaType.APPLICATION_JSON)
                        .queryParam("view", "data")
                        .queryParam("short", "true"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(jsonContent, true)
                );

        verify(this.repository).findAll();
    }

    @Test
    @DisplayName("GET /api/v1/tracktime/stats?view=summary&short=true")
    void handleGetStats_whenViewTypeIsSummary_shortFormatIsTrue_returnsOnlySummary() throws Exception {
        when(this.repository.findAll()).thenReturn(this.trackTimeStats);

        List<TrackTimeDto> dtoList = this.trackTimeStats.stream().map(this.toDtoMapper.toShort()).toList();

        var statistics = dtoList.stream().mapToLong(TrackTimeDto::getExecutionTime).summaryStatistics();

        Map<String, Object> resultBody = Map.of("summary", this.statisticsToSummaryMapper.apply(statistics));
        String jsonContent = this.objectMapper.writeValueAsString(resultBody);

        this.mockMvc.perform(get("/api/v1/tracktime/stats").contentType(MediaType.APPLICATION_JSON)
                        .queryParam("view", "summary")
                        .queryParam("short", "true"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(jsonContent, true)
                );

        verify(this.repository).findAll();
    }

    @Test
    @DisplayName("POST /api/v1/tracktime/stats :: json body is null")
    void handlePostSearch_whenRequestBodyIsNull_returnsErrorMessageSearchPropertiesMustBeSet() throws Exception {
        this.mockMvc.perform(post("/api/v1/tracktime/stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                  "statusCode": 400,
                                  "message": "At least one search property must be specified"
                                }
                                """)
                );

        verifyNoInteractions(this.repository);
    }

    @Test
    @DisplayName("POST /api/v1/tracktime/stats :: empty json body")
    void handlePostSearch_whenRequestBodyIsEmpty_returnsErrorMessageSearchPropertiesMustBeSet() throws Exception {
        this.mockMvc.perform(post("/api/v1/tracktime/stats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                  "statusCode": 400,
                                  "message": "At least one search property must be specified"
                                }
                                """)
                );

        verifyNoInteractions(this.repository);
    }
}