package com.recruitment.job.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitment.job.dto.*;
import com.recruitment.job.entity.JobStatus;
import com.recruitment.job.service.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JobService jobService;

    private JobResponse sampleJobResponse;

    @BeforeEach
    void setUp() {
        sampleJobResponse = JobResponse.builder()
                .id(1L)
                .companyId(10L)
                .title("Senior Backend Engineer")
                .description("Java and Spring Boot microservices")
                .requiredSkills("Java, Spring Boot, MySQL")
                .location("San Francisco, CA")
                .salaryRange("$140k - $180k")
                .jobType("FULL_TIME")
                .status(JobStatus.OPEN)
                .applicationCount(0L)
                .build();
    }

    @Test
    void shouldPostJob() throws Exception {
        CreateJobRequest request = CreateJobRequest.builder()
                .companyId(10L)
                .title("Senior Backend Engineer")
                .description("Java and Spring Boot microservices")
                .requiredSkills("Java, Spring Boot, MySQL")
                .location("San Francisco, CA")
                .salaryRange("$140k - $180k")
                .jobType("FULL_TIME")
                .build();

        when(jobService.createJob(any(CreateJobRequest.class), any(), any())).thenReturn(sampleJobResponse);

        mockMvc.perform(post("/api/jobs")
                        .header("X-API-KEY", "job-service-secret-key-12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Senior Backend Engineer"));
    }

    @Test
    void shouldListAllJobs() throws Exception {
        when(jobService.getAllJobs(null)).thenReturn(List.of(sampleJobResponse));

        mockMvc.perform(get("/api/jobs")
                        .header("X-API-KEY", "job-service-secret-key-12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1L));
    }

    @Test
    void shouldGetJobById() throws Exception {
        when(jobService.getJobById(1L)).thenReturn(sampleJobResponse);

        mockMvc.perform(get("/api/jobs/1")
                        .header("X-API-KEY", "job-service-secret-key-12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    void shouldRejectRequestWithoutApiKeyOrUserContext() throws Exception {
        mockMvc.perform(get("/api/jobs"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }
}
