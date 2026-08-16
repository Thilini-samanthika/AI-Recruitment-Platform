package com.recruitment.candidate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitment.candidate.dto.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CandidateControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String API_KEY = "candidate-service-secret-key-12345";

    @Test
    @Order(1)
    void shouldRejectRequestWithoutApiKeyOrUserHeader() throws Exception {
        mockMvc.perform(get("/api/candidates"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @Order(2)
    void shouldRejectRequestWithInvalidApiKey() throws Exception {
        mockMvc.perform(get("/api/candidates")
                        .header("X-API-KEY", "wrong-key"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @Order(3)
    void shouldAllowAccessToSwaggerDocsWithoutApiKey() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists());
    }

    @Test
    @Order(4)
    void shouldCreateCandidateProfileWithApiKey() throws Exception {
        CreateCandidateRequest request = CreateCandidateRequest.builder()
                .userId(1L)
                .fullName("Alex Rivera")
                .phone("+1-555-8888")
                .address("Seattle, WA")
                .headline("Senior Cloud Architect")
                .summary("10 years experience building scalable systems.")
                .build();

        mockMvc.perform(post("/api/candidates")
                        .header("X-API-KEY", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("Alex Rivera"))
                .andExpect(jsonPath("$.data.userId").value(1));
    }

    @Test
    @Order(5)
    void shouldGetCandidateByIdWithGatewayHeader() throws Exception {
        mockMvc.perform(get("/api/candidates/1")
                        .header("X-User-Id", "1")
                        .header("X-User-Role", "ROLE_CANDIDATE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("Alex Rivera"));
    }

    @Test
    @Order(6)
    void shouldGetCandidateByUserId() throws Exception {
        mockMvc.perform(get("/api/candidates/user/1")
                        .header("X-API-KEY", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @Order(7)
    void shouldAddSkillToCandidate() throws Exception {
        SkillRequest skillReq = SkillRequest.builder()
                .skillName("Kubernetes")
                .proficiencyLevel("EXPERT")
                .build();

        mockMvc.perform(post("/api/candidates/1/skills")
                        .header("X-User-Id", "1")
                        .header("X-User-Role", "ROLE_CANDIDATE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skillReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.skillName").value("Kubernetes"))
                .andExpect(jsonPath("$.data.proficiencyLevel").value("EXPERT"));
    }

    @Test
    @Order(8)
    void shouldListCandidateSkills() throws Exception {
        mockMvc.perform(get("/api/candidates/1/skills")
                        .header("X-API-KEY", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @Order(9)
    void shouldAddEducationToCandidate() throws Exception {
        EducationRequest eduReq = EducationRequest.builder()
                .institution("Stanford University")
                .degree("Bachelor of Science")
                .fieldOfStudy("Computer Science")
                .startDate(LocalDate.of(2014, 9, 1))
                .endDate(LocalDate.of(2018, 6, 1))
                .build();

        mockMvc.perform(post("/api/candidates/1/education")
                        .header("X-User-Id", "1")
                        .header("X-User-Role", "ROLE_CANDIDATE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eduReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.institution").value("Stanford University"));
    }

    @Test
    @Order(10)
    void shouldAddExperienceToCandidate() throws Exception {
        ExperienceRequest expReq = ExperienceRequest.builder()
                .companyName("Amazon Web Services")
                .jobTitle("Cloud Solutions Architect")
                .startDate(LocalDate.of(2018, 7, 1))
                .endDate(LocalDate.of(2023, 12, 1))
                .description("Architected multi-region AWS cloud infrastructures.")
                .build();

        mockMvc.perform(post("/api/candidates/1/experience")
                        .header("X-User-Id", "1")
                        .header("X-User-Role", "ROLE_CANDIDATE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.companyName").value("Amazon Web Services"));
    }

    @Test
    @Order(11)
    void shouldUpdateCandidateProfile() throws Exception {
        UpdateCandidateRequest updateReq = UpdateCandidateRequest.builder()
                .fullName("Alex Rivera (Updated)")
                .phone("+1-555-7777")
                .address("Seattle, WA")
                .headline("Principal Cloud Architect")
                .summary("Updated principal architect bio.")
                .build();

        mockMvc.perform(put("/api/candidates/1")
                        .header("X-User-Id", "1")
                        .header("X-User-Role", "ROLE_CANDIDATE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("Alex Rivera (Updated)"))
                .andExpect(jsonPath("$.data.headline").value("Principal Cloud Architect"));
    }

    @Test
    @Order(12)
    void shouldListAllCandidates() throws Exception {
        mockMvc.perform(get("/api/candidates")
                        .header("X-API-KEY", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
    }
}
