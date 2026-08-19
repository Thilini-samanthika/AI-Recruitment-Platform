package com.recruitment.candidate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitment.candidate.dto.*;
import com.recruitment.candidate.security.ApiKeyFilter;
import com.recruitment.candidate.service.CandidateService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
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

    @MockBean
    private CandidateService candidateService;

    private static final String API_KEY = "candidate-service-secret-key-12345";
    private static final String CANDIDATE_ID = "66c3abc1234567890abcdef1";

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

        CandidateResponse response = CandidateResponse.builder()
                .id(CANDIDATE_ID)
                .userId(1L)
                .fullName("Alex Rivera")
                .phone("+1-555-8888")
                .address("Seattle, WA")
                .headline("Senior Cloud Architect")
                .summary("10 years experience building scalable systems.")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .skills(new ArrayList<>())
                .educations(new ArrayList<>())
                .experiences(new ArrayList<>())
                .build();

        when(candidateService.createCandidate(any(CreateCandidateRequest.class), any())).thenReturn(response);

        mockMvc.perform(post("/api/candidates")
                        .header("X-API-KEY", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("Alex Rivera"))
                .andExpect(jsonPath("$.data.id").value(CANDIDATE_ID));
    }

    @Test
    @Order(5)
    void shouldRejectCandidateCreationWhenValidationFails() throws Exception {
        CreateCandidateRequest invalidRequest = CreateCandidateRequest.builder()
                .fullName("") // Blank name violates @NotBlank
                .build();

        mockMvc.perform(post("/api/candidates")
                        .header("X-API-KEY", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.data.fullName").exists());
    }

    @Test
    @Order(6)
    void shouldGetCandidateByIdWithGatewayHeader() throws Exception {
        CandidateResponse response = CandidateResponse.builder()
                .id(CANDIDATE_ID)
                .userId(1L)
                .fullName("Alex Rivera")
                .build();

        when(candidateService.getCandidateById(CANDIDATE_ID)).thenReturn(response);

        mockMvc.perform(get("/api/candidates/" + CANDIDATE_ID)
                        .header("X-User-Id", "1")
                        .header("X-User-Role", "ROLE_CANDIDATE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("Alex Rivera"))
                .andExpect(jsonPath("$.data.id").value(CANDIDATE_ID));
    }

    @Test
    @Order(7)
    void shouldGetCandidateByUserId() throws Exception {
        CandidateResponse response = CandidateResponse.builder()
                .id(CANDIDATE_ID)
                .userId(1L)
                .fullName("Alex Rivera")
                .build();

        when(candidateService.getCandidateByUserId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/candidates/user/1")
                        .header("X-API-KEY", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(CANDIDATE_ID));
    }

    @Test
    @Order(8)
    void shouldAddSkillToCandidate() throws Exception {
        SkillRequest skillReq = SkillRequest.builder()
                .skillName("Kubernetes")
                .proficiencyLevel("EXPERT")
                .build();

        SkillResponse skillRes = SkillResponse.builder()
                .id("skill-123")
                .candidateId(CANDIDATE_ID)
                .skillName("Kubernetes")
                .proficiencyLevel("EXPERT")
                .build();

        when(candidateService.addSkill(eq(CANDIDATE_ID), any(SkillRequest.class), eq(1L), eq("ROLE_CANDIDATE")))
                .thenReturn(skillRes);

        mockMvc.perform(post("/api/candidates/" + CANDIDATE_ID + "/skills")
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
    @Order(9)
    void shouldListCandidateSkills() throws Exception {
        SkillResponse skillRes = SkillResponse.builder()
                .id("skill-123")
                .candidateId(CANDIDATE_ID)
                .skillName("Kubernetes")
                .proficiencyLevel("EXPERT")
                .build();

        when(candidateService.getSkills(CANDIDATE_ID)).thenReturn(List.of(skillRes));

        mockMvc.perform(get("/api/candidates/" + CANDIDATE_ID + "/skills")
                        .header("X-API-KEY", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    @Order(10)
    void shouldAddEducationToCandidate() throws Exception {
        EducationRequest eduReq = EducationRequest.builder()
                .institution("Stanford University")
                .degree("Bachelor of Science")
                .fieldOfStudy("Computer Science")
                .startDate(LocalDate.of(2014, 9, 1))
                .endDate(LocalDate.of(2018, 6, 1))
                .build();

        EducationResponse eduRes = EducationResponse.builder()
                .id("edu-123")
                .candidateId(CANDIDATE_ID)
                .institution("Stanford University")
                .degree("Bachelor of Science")
                .fieldOfStudy("Computer Science")
                .build();

        when(candidateService.addEducation(eq(CANDIDATE_ID), any(EducationRequest.class), eq(1L), eq("ROLE_CANDIDATE")))
                .thenReturn(eduRes);

        mockMvc.perform(post("/api/candidates/" + CANDIDATE_ID + "/education")
                        .header("X-User-Id", "1")
                        .header("X-User-Role", "ROLE_CANDIDATE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eduReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.institution").value("Stanford University"));
    }

    @Test
    @Order(11)
    void shouldAddExperienceToCandidate() throws Exception {
        ExperienceRequest expReq = ExperienceRequest.builder()
                .companyName("Amazon Web Services")
                .jobTitle("Cloud Solutions Architect")
                .startDate(LocalDate.of(2018, 7, 1))
                .endDate(LocalDate.of(2023, 12, 1))
                .description("Architected multi-region AWS cloud infrastructures.")
                .build();

        ExperienceResponse expRes = ExperienceResponse.builder()
                .id("exp-123")
                .candidateId(CANDIDATE_ID)
                .companyName("Amazon Web Services")
                .jobTitle("Cloud Solutions Architect")
                .build();

        when(candidateService.addExperience(eq(CANDIDATE_ID), any(ExperienceRequest.class), eq(1L), eq("ROLE_CANDIDATE")))
                .thenReturn(expRes);

        mockMvc.perform(post("/api/candidates/" + CANDIDATE_ID + "/experience")
                        .header("X-User-Id", "1")
                        .header("X-User-Role", "ROLE_CANDIDATE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.companyName").value("Amazon Web Services"));
    }

    @Test
    @Order(12)
    void shouldUpdateCandidateProfile() throws Exception {
        UpdateCandidateRequest updateReq = UpdateCandidateRequest.builder()
                .fullName("Alex Rivera (Updated)")
                .phone("+1-555-7777")
                .address("Seattle, WA")
                .headline("Principal Cloud Architect")
                .summary("Updated principal architect bio.")
                .build();

        CandidateResponse updatedRes = CandidateResponse.builder()
                .id(CANDIDATE_ID)
                .userId(1L)
                .fullName("Alex Rivera (Updated)")
                .headline("Principal Cloud Architect")
                .build();

        when(candidateService.updateCandidate(eq(CANDIDATE_ID), any(UpdateCandidateRequest.class), eq(1L), eq("ROLE_CANDIDATE")))
                .thenReturn(updatedRes);

        mockMvc.perform(put("/api/candidates/" + CANDIDATE_ID)
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
    @Order(13)
    void shouldListAllCandidates() throws Exception {
        CandidateResponse res = CandidateResponse.builder()
                .id(CANDIDATE_ID)
                .userId(1L)
                .fullName("Alex Rivera")
                .build();

        when(candidateService.getAllCandidates()).thenReturn(List.of(res));

        mockMvc.perform(get("/api/candidates")
                        .header("X-API-KEY", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)));
    }
}
