package com.recruitment.company.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitment.company.config.SecurityConfig;
import com.recruitment.company.dto.CreateRecruiterRequest;
import com.recruitment.company.dto.RecruiterResponse;
import com.recruitment.company.entity.RecruiterStatus;
import com.recruitment.company.security.ApiKeyFilter;
import com.recruitment.company.service.RecruiterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecruiterController.class)
@Import({SecurityConfig.class, ApiKeyFilter.class})
@ActiveProfiles("test")
class RecruiterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RecruiterService recruiterService;

    private RecruiterResponse sampleRecruiterResponse;

    @BeforeEach
    void setUp() {
        sampleRecruiterResponse = RecruiterResponse.builder()
                .id("recruiter-1")
                .companyId("66c25a1f2b3e8c0012345678")
                .departmentId("dept-1")
                .fullName("Jane Doe")
                .email("jane.doe@acmetech.io")
                .phone("+1 (555) 345-6789")
                .title("Senior Recruiter")
                .status(RecruiterStatus.ACTIVE)
                .build();
    }

    @Test
    void shouldCreateRecruiter() throws Exception {
        CreateRecruiterRequest request = CreateRecruiterRequest.builder()
                .fullName("Jane Doe")
                .email("jane.doe@acmetech.io")
                .phone("+1 (555) 345-6789")
                .title("Senior Recruiter")
                .departmentId("dept-1")
                .build();

        when(recruiterService.createRecruiter(eq("66c25a1f2b3e8c0012345678"), any(CreateRecruiterRequest.class), any(), any()))
                .thenReturn(sampleRecruiterResponse);

        mockMvc.perform(post("/api/companies/66c25a1f2b3e8c0012345678/recruiters")
                        .header("X-API-KEY", "company-service-secret-key-12345")
                        .header("X-User-Id", "100")
                        .header("X-User-Role", "ROLE_COMPANY")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("Jane Doe"));
    }

    @Test
    void shouldListRecruiters() throws Exception {
        when(recruiterService.getRecruitersByCompanyId("66c25a1f2b3e8c0012345678"))
                .thenReturn(List.of(sampleRecruiterResponse));

        mockMvc.perform(get("/api/companies/66c25a1f2b3e8c0012345678/recruiters")
                        .header("X-API-KEY", "company-service-secret-key-12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("recruiter-1"));
    }
}
