package com.recruitment.job.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitment.job.dto.*;
import com.recruitment.job.entity.ApplicationStatus;
import com.recruitment.job.entity.JobStatus;
import com.recruitment.job.repository.ApplicationRepository;
import com.recruitment.job.repository.JobRepository;
import com.recruitment.job.service.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    @MockBean
    private JobRepository jobRepository;

    @MockBean
    private ApplicationRepository applicationRepository;

    @MockBean
    private MongoTemplate mongoTemplate;

    @MockBean
    private GridFsTemplate gridFsTemplate;

    @MockBean
    private MappingMongoConverter mappingMongoConverter;

    private JobResponse sampleJobResponse;
    private ApplicationResponse sampleAppResponse;

    @BeforeEach
    void setUp() {
        sampleJobResponse = JobResponse.builder()
                .id("66c3abc1234567890abcdef1")
                .companyId(10L)
                .title("Senior Backend Engineer")
                .description("Java and Spring Boot microservices with MongoDB")
                .requiredSkills("Java, Spring Boot, MongoDB")
                .location("San Francisco, CA")
                .salaryRange("$140,000 - $180,000 / yr")
                .jobType("FULL_TIME")
                .status(JobStatus.OPEN)
                .applicationCount(0L)
                .deadline(LocalDateTime.now().plusDays(30))
                .build();

        sampleAppResponse = ApplicationResponse.builder()
                .id("66c3abc1234567890abcdef2")
                .jobId("66c3abc1234567890abcdef1")
                .jobTitle("Senior Backend Engineer")
                .companyId(10L)
                .candidateId(1L)
                .status(ApplicationStatus.APPLIED)
                .notes("Interested in position")
                .build();
    }

    @Test
    @DisplayName("Should post a new job with valid role and API key")
    void shouldPostJob() throws Exception {
        CreateJobRequest request = CreateJobRequest.builder()
                .companyId(10L)
                .title("Senior Backend Engineer")
                .description("Java and Spring Boot microservices with MongoDB")
                .requiredSkills("Java, Spring Boot, MongoDB")
                .location("San Francisco, CA")
                .salaryRange("$140,000 - $180,000 / yr")
                .jobType("FULL_TIME")
                .deadline(LocalDateTime.now().plusDays(30))
                .build();

        when(jobService.createJob(any(CreateJobRequest.class), any(), any())).thenReturn(sampleJobResponse);

        mockMvc.perform(post("/api/jobs")
                        .header("X-API-KEY", "job-service-secret-key-12345")
                        .header("X-User-Id", "10")
                        .header("X-User-Role", "ROLE_COMPANY")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Senior Backend Engineer"));
    }

    @Test
    @DisplayName("Should list all jobs")
    void shouldListAllJobs() throws Exception {
        when(jobService.getAllJobs(null)).thenReturn(List.of(sampleJobResponse));

        mockMvc.perform(get("/api/jobs")
                        .header("X-API-KEY", "job-service-secret-key-12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("66c3abc1234567890abcdef1"));
    }

    @Test
    @DisplayName("Should get job by ID")
    void shouldGetJobById() throws Exception {
        when(jobService.getJobById("66c3abc1234567890abcdef1")).thenReturn(sampleJobResponse);

        mockMvc.perform(get("/api/jobs/66c3abc1234567890abcdef1")
                        .header("X-API-KEY", "job-service-secret-key-12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("66c3abc1234567890abcdef1"));
    }

    @Test
    @DisplayName("Should search jobs by multiple query parameters")
    void shouldSearchJobs() throws Exception {
        when(jobService.searchJobs("Backend", "San Francisco", "FULL_TIME", JobStatus.OPEN))
                .thenReturn(List.of(sampleJobResponse));

        mockMvc.perform(get("/api/jobs/search")
                        .header("X-API-KEY", "job-service-secret-key-12345")
                        .param("keyword", "Backend")
                        .param("location", "San Francisco")
                        .param("jobType", "FULL_TIME")
                        .param("status", "OPEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("Senior Backend Engineer"));
    }

    @Test
    @DisplayName("Should apply to job successfully")
    void shouldApplyToJob() throws Exception {
        ApplyJobRequest request = ApplyJobRequest.builder()
                .candidateId(1L)
                .notes("Strong background in Java")
                .resumeUrl("https://example.com/resume.pdf")
                .build();

        when(jobService.applyToJob(eq("66c3abc1234567890abcdef1"), any(ApplyJobRequest.class), any(), any()))
                .thenReturn(sampleAppResponse);

        mockMvc.perform(post("/api/jobs/66c3abc1234567890abcdef1/apply")
                        .header("X-API-KEY", "job-service-secret-key-12345")
                        .header("X-User-Id", "1")
                        .header("X-User-Role", "ROLE_CANDIDATE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("66c3abc1234567890abcdef2"));
    }

    @Test
    @DisplayName("Should update application status")
    void shouldUpdateApplicationStatus() throws Exception {
        UpdateApplicationStatusRequest request = UpdateApplicationStatusRequest.builder()
                .status(ApplicationStatus.SHORTLISTED)
                .build();

        ApplicationResponse updatedResponse = ApplicationResponse.builder()
                .id("66c3abc1234567890abcdef2")
                .status(ApplicationStatus.SHORTLISTED)
                .build();

        when(jobService.updateApplicationStatus(eq("66c3abc1234567890abcdef2"), any(UpdateApplicationStatusRequest.class), any(), any()))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/api/jobs/applications/66c3abc1234567890abcdef2/status")
                        .header("X-API-KEY", "job-service-secret-key-12345")
                        .header("X-User-Id", "10")
                        .header("X-User-Role", "ROLE_COMPANY")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("SHORTLISTED"));
    }

    @Test
    @DisplayName("Should reject unauthenticated requests")
    void shouldRejectRequestWithoutApiKeyOrUserContext() throws Exception {
        mockMvc.perform(get("/api/jobs"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should return 400 Bad Request on DTO validation failure")
    void shouldReturnBadRequestWhenValidationFails() throws Exception {
        CreateJobRequest invalidRequest = CreateJobRequest.builder()
                .companyId(null) // missing required company ID
                .title("A") // invalid: min 3 chars
                .build();

        mockMvc.perform(post("/api/jobs")
                        .header("X-API-KEY", "job-service-secret-key-12345")
                        .header("X-User-Id", "10")
                        .header("X-User-Role", "ROLE_COMPANY")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data.title").exists());
    }
}
