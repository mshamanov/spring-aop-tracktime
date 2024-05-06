package com.mash.aoptracktime.rest.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@Sql(value = "classpath:test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(statements = "DELETE from tracktimestats", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class TrackTimeRestControllerIT {
    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("POST /api/tracktimestats/search :: {\"className\": \"JavaService\"}")
    void handlePostSearch_whenClassNameSelected_returnsStatsForSelectedClassName() throws Exception {
        this.mockMvc.perform(post("/api/tracktimestats/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"className": "JavaService"}
                                """))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.result").isArray(),
                        jsonPath("$.result.length()").value(2),
                        jsonPath("$.summary").isNotEmpty(),
                        jsonPath("$.summary.count").value(2),
                        jsonPath("$.summary.min").value(85),
                        jsonPath("$.summary.max").value(120),
                        jsonPath("$.summary.average").value(102.5),
                        content().json("""
                                {"result": [{"className": "JavaService"}, {"className": "JavaService"}]}
                                """)
                );
    }

    @Test
    @DisplayName("POST /api/tracktimestats/search :: {\"className\": \"Java*\"}")
    void handlePostSearch_whenClassNameMaskSelected_returnsStatsForSelectedClassNameMask() throws Exception {
        this.mockMvc.perform(post("/api/tracktimestats/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"className": "Java*"}
                                """))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.result").isArray(),
                        jsonPath("$.result.length()").value(6),
                        jsonPath("$.summary").isNotEmpty(),
                        jsonPath("$.summary.count").value(6),
                        jsonPath("$.summary.min").value(23),
                        jsonPath("$.summary.max").value(120),
                        jsonPath("$.summary.average").value(68.33)
                );
    }

    @Test
    @DisplayName("POST /api/tracktimestats/search :: {\"groupName\": \"sync\"}")
    void handlePostSearch_whenGroupNameSelected_returnsStatsForSelectedGroupName() throws Exception {
        this.mockMvc.perform(post("/api/tracktimestats/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"groupName": "sync"}
                                """))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.result").isArray(),
                        jsonPath("$.result.length()").value(2),
                        jsonPath("$.summary").isNotEmpty(),
                        jsonPath("$.summary.count").value(2),
                        jsonPath("$.summary.min").value(82),
                        jsonPath("$.summary.max").value(105),
                        jsonPath("$.summary.average").value(93.5),
                        content().json("""
                                {"result": [{"groupName": "sync"}, {"groupName": "sync"}]}
                                """)
                );
    }

    @Test
    @DisplayName("POST /api/tracktimestats/search :: {\"methodName\": \"addClass\"}")
    void handlePostSearch_whenMethodNameSelected_returnsStatsForSelectedMethodName() throws Exception {
        this.mockMvc.perform(post("/api/tracktimestats/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"methodName": "addClass"}
                                """))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.result").isArray(),
                        jsonPath("$.result.length()").value(2),
                        jsonPath("$.summary").isNotEmpty(),
                        jsonPath("$.summary.count").value(2),
                        jsonPath("$.summary.min").value(23),
                        jsonPath("$.summary.max").value(85),
                        jsonPath("$.summary.average").value(54),
                        content().json("""
                                {"result": [{"className": "JavaService","methodName": "addClass"},
                                            {"className": "JavaScriptService", "methodName": "addClass"}]}
                                """)
                );
    }

    @Test
    @DisplayName("POST /api/tracktimestats/search :: {\"methodName\": \"add*\"}")
    void handlePostSearch_whenMethodNameMaskSelected_returnsStatsForSelectedMethodNameMask() throws Exception {
        this.mockMvc.perform(post("/api/tracktimestats/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"methodName": "add*"}
                                """))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.result").isArray(),
                        jsonPath("$.result.length()").value(7),
                        jsonPath("$.summary").isNotEmpty(),
                        jsonPath("$.summary.count").value(7),
                        jsonPath("$.summary.min").value(23),
                        jsonPath("$.summary.max").value(120),
                        jsonPath("$.summary.average").value(74)
                );
    }

    @Test
    @DisplayName("POST /api/tracktimestats/search :: {\"packageName\": \"com.computers.lang.python.repo\"}")
    void handlePostSearch_whenPackageNameSelected_returnsStatsForSelectedPackageName() throws Exception {
        this.mockMvc.perform(post("/api/tracktimestats/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"packageName": "com.computers.lang.python.repo"}
                                """))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.result").isArray(),
                        jsonPath("$.result.length()").value(3),
                        jsonPath("$.summary").isNotEmpty(),
                        jsonPath("$.summary.count").value(3),
                        jsonPath("$.summary.min").value(82),
                        jsonPath("$.summary.max").value(123),
                        jsonPath("$.summary.average").value(103.33),
                        content().json("""
                                {
                                  "result": [{"packageName": "com.computers.lang.python.repo","methodName": "addSuperClass"},
                                             {"packageName": "com.computers.lang.python.repo","methodName": "addFunction"},
                                             {"packageName": "com.computers.lang.python.repo","methodName": "removeFunction"}]
                                }
                                """)
                );
    }

    @Test
    @DisplayName("POST /api/tracktimestats/search :: {\"packageName\": \"com.computers.lang.*.repo\"}")
    void handlePostSearch_whenPackageNameMaskSelected_returnsStatsForSelectedPackageNameMask() throws Exception {
        this.mockMvc.perform(post("/api/tracktimestats/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"packageName": "com.computers.lang.*.repo"}
                                """))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.result").isArray(),
                        jsonPath("$.result.length()").value(4),
                        jsonPath("$.summary").isNotEmpty(),
                        jsonPath("$.summary.count").value(4),
                        jsonPath("$.summary.min").value(79),
                        jsonPath("$.summary.max").value(123),
                        jsonPath("$.summary.average").value(97.25)
                );
    }

    @Test
    @DisplayName("POST /api/tracktimestats/search :: {\"parameters\": \"Method\"}")
    void handlePostSearch_whenParametersSelected_returnsStatsForSelectedParameters() throws Exception {
        this.mockMvc.perform(post("/api/tracktimestats/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"parameters": "Method"}
                                """))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.result").isArray(),
                        jsonPath("$.result.length()").value(1),
                        jsonPath("$.summary").isNotEmpty(),
                        jsonPath("$.summary.count").value(1),
                        jsonPath("$.summary.min").value(48),
                        jsonPath("$.summary.max").value(48),
                        jsonPath("$.summary.average").value(48),
                        content().json("""
                                {
                                  "result": [{"parameters": "Method"}]
                                }
                                """)
                );
    }

    @Test
    @DisplayName("POST /api/tracktimestats/search :: {\"returnType\": \"boolean\"}")
    void handlePostSearch_whenReturnTypeSelected_returnsStatsForSelectedReturnType() throws Exception {
        this.mockMvc.perform(post("/api/tracktimestats/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"returnType": "boolean"}
                                """))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.result").isArray(),
                        jsonPath("$.result.length()").value(1),
                        jsonPath("$.summary").isNotEmpty(),
                        jsonPath("$.summary.count").value(1),
                        jsonPath("$.summary.min").value(123),
                        jsonPath("$.summary.max").value(123),
                        jsonPath("$.summary.average").value(123),
                        content().json("""
                                {
                                  "result": [{"returnType": "boolean"}]
                                }
                                """)
                );
    }

    @Test
    @DisplayName("POST /api/tracktimestats/search :: {\"status\": \"exception\"}")
    void handlePostSearch_whenMethodStatusSelected_returnsStatsForSelectedMethodStatus() throws Exception {
        this.mockMvc.perform(post("/api/tracktimestats/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status": "exception"}
                                """))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.result").isArray(),
                        jsonPath("$.result.length()").value(2),
                        jsonPath("$.summary").isNotEmpty(),
                        jsonPath("$.summary.count").value(2),
                        jsonPath("$.summary.min").value(79),
                        jsonPath("$.summary.max").value(123),
                        jsonPath("$.summary.average").value(101),
                        content().json("""
                                {
                                  "result": [{"status": "exception"}, {"status": "exception"}]
                                }
                                """)
                );
    }

    @Test
    @DisplayName("POST /api/tracktimestats/search :: {\"status\": \"error\"}")
    void handlePostSearch_whenMethodStatusInvalid_returnsErrorMessageStatusInvalid() throws Exception {
        this.mockMvc.perform(post("/api/tracktimestats/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status": "error"}
                                """))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.result").doesNotExist(),
                        jsonPath("$.summary").doesNotExist(),
                        jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()),
                        jsonPath("$.timestamp").isNotEmpty(),
                        jsonPath("$.message").value(
                                Matchers.containsString("Specified track time method status is invalid"))
                );
    }

    @Test
    @DisplayName("POST /api/tracktimestats/search :: {\"createdAt\": \"11-05-2024\"}")
    void handlePostSearch_whenCreatedAtSelected_returnsStatsForSelectedCreatedAt() throws Exception {
        this.mockMvc.perform(post("/api/tracktimestats/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"createdAt": "11-05-2024"}
                                """))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.result").isArray(),
                        jsonPath("$.result.length()").value(1),
                        jsonPath("$.summary").isNotEmpty(),
                        jsonPath("$.summary.count").value(1),
                        jsonPath("$.summary.min").value(23),
                        jsonPath("$.summary.max").value(23),
                        jsonPath("$.summary.average").value(23),
                        content().json("""
                                {
                                  "result": [{"createdAt": "11-05-2024 12:41:00"}]
                                }
                                """)
                );
    }

    @Test
    @DisplayName("POST /api/tracktimestats/search :: {\"createdAt\": \"15-05-202\"}")
    void handlePostSearch_whenCreatedAtInvalid_returnsErrorMessageCreatedAtFormatInvalid() throws Exception {
        this.mockMvc.perform(post("/api/tracktimestats/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"createdAt": "15-05-202"}
                                """))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.result").doesNotExist(),
                        jsonPath("$.summary").doesNotExist(),
                        jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()),
                        jsonPath("$.timestamp").isNotEmpty(),
                        jsonPath("$.message").value(Matchers.containsString("Could not parse date"))
                );
    }

    @Test
    @DisplayName("POST /api/tracktimestats/search :: empty {}")
    void handlePostSearch_whenSearchParametersNotSpecified_returnsErrorMessageParametersMustBeSpecified() throws Exception {
        this.mockMvc.perform(post("/api/tracktimestats/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.result").doesNotExist(),
                        jsonPath("$.summary").doesNotExist(),
                        jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()),
                        jsonPath("$.timestamp").isNotEmpty(),
                        jsonPath("$.message").value(
                                Matchers.containsString("At least one search property must be specified"))
                );
    }

    @Test
    @DisplayName("POST /api/tracktimestats/search :: {\"parameters\": \"\"}")
    void handlePostSearch_whenParameterIsEmptyString_returnsStatsWithSelectedParameterAsEmptyString() throws Exception {
        this.mockMvc.perform(post("/api/tracktimestats/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"parameters": ""}
                                """))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.result").isArray(),
                        jsonPath("$.result.length()").value(1),
                        jsonPath("$.summary").isNotEmpty(),
                        jsonPath("$.summary.count").value(1),
                        jsonPath("$.summary.min").value(79),
                        jsonPath("$.summary.max").value(79),
                        jsonPath("$.summary.average").value(79),
                        content().json("""
                                {
                                  "result": [{"className": "JavaScriptService"}]
                                }
                                """)
                );
    }

    @Test
    @DisplayName("POST /api/tracktimestats/search :: {\"startDate\": \"11-05-2024\", \"endDate\": \"14-05-2024\"}")
    void handlePostSearch_whenStartAndEndDatesSelected_returnsStatsForBetweenDates() throws Exception {
        this.mockMvc.perform(post("/api/tracktimestats/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"startDate": "11-05-2024",
                                "endDate": "14-05-2024"}
                                """))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.result").isArray(),
                        jsonPath("$.result.length()").value(5),
                        jsonPath("$.summary").isNotEmpty(),
                        jsonPath("$.summary.count").value(5),
                        jsonPath("$.summary.min").value(23),
                        jsonPath("$.summary.max").value(105),
                        jsonPath("$.summary.average").value(67.4)
                );
    }

    @Test
    @DisplayName("POST /api/tracktimestats/search :: {\"startDate\": \"11-05-2024\"}")
    void handlePostSearch_whenStartDateSelected_returnsStatsAfterSelectedDateIncluded() throws Exception {
        this.mockMvc.perform(post("/api/tracktimestats/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"startDate": "11-05-2024"}
                                """))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.result").isArray(),
                        jsonPath("$.result.length()").value(6),
                        jsonPath("$.summary").isNotEmpty(),
                        jsonPath("$.summary.count").value(6),
                        jsonPath("$.summary.min").value(23),
                        jsonPath("$.summary.max").value(123),
                        jsonPath("$.summary.average").value(76.67)
                );
    }

    @Test
    @DisplayName("POST /api/tracktimestats/search :: {\"endDate\": \"14-05-2024\"}")
    void handlePostSearch_whenEndDateSelected_returnsStatsBeforeSelectedDateIncluded() throws Exception {
        this.mockMvc.perform(post("/api/tracktimestats/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"endDate": "14-05-2024"}
                                """))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.result").isArray(),
                        jsonPath("$.result.length()").value(8),
                        jsonPath("$.summary").isNotEmpty(),
                        jsonPath("$.summary.count").value(8),
                        jsonPath("$.summary.min").value(23),
                        jsonPath("$.summary.max").value(120),
                        jsonPath("$.summary.average").value(74.63)
                );
    }
}