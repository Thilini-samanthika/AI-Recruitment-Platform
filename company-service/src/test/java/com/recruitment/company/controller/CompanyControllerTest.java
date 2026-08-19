package com.recruitment.company.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitment.company.config.SecurityConfig;
import com.recruitment.company.dto.*;
import com.recruitment.company.entity.VerificationStatus;
import com.recruitment.company.security.ApiKeyFilter;
import com.recruitment.company.service.CompanyService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CompanyController.class)
@Import({SecurityConfig.class, ApiKeyFilter.class})
@ActiveProfiles("test")
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CompanyService companyService;

    private CompanyResponse sampleCompanyResponse;

    @BeforeEach
    void setUp() {
        sampleCompanyResponse = CompanyResponse.builder()
                .id("66c25a1f2b3e8c0012345678")
                .userId(100L)
                .companyName("Acme Technologies Inc.")
                .email("contact@acmecorp.com")
                .phone("+1 (555) 234-5678")
                .address("100 Innovation Way, San Francisco, CA")
                .profile(CompanyProfileResponse.builder()
                        .industry("Information Technology")
                        .companySize("51-200")
                        .website("https://acmecorp.com")
                        .build())
                .verification(VerificationResponse.builder()
                        .status(VerificationStatus.UNVERIFIED)
                        .build())
                .build();
    }

    @Test
    void shouldRegisterCompany() throws Exception {
        CreateCompanyRequest request = CreateCompanyRequest.builder()
                .userId(100L)
                .companyName("Acme Technologies Inc.")
                .email("contact@acmecorp.com")
                .phone("+1 (555) 234-5678")
                .address("100 Innovation Way, San Francisco, CA")
                .build();

        when(companyService.registerCompany(any(CreateCompanyRequest.class), any(), any())).thenReturn(sampleCompanyResponse);

        mockMvc.perform(post("/api/companies")
                        .header("X-API-KEY", "company-service-secret-key-12345")
                        .header("X-User-Id", "100")
                        .header("X-User-Role", "ROLE_COMPANY")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.companyName").value("Acme Technologies Inc."));
    }

    @Test
    void shouldRejectInvalidRegistrationPayload() throws Exception {
        CreateCompanyRequest invalidRequest = CreateCompanyRequest.builder()
                .companyName("") // Blank name
                .email("not-an-email")
                .build();

        mockMvc.perform(post("/api/companies")
                        .header("X-API-KEY", "company-service-secret-key-12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data.companyName").exists())
                .andExpect(jsonPath("$.data.email").exists())
                .andExpect(jsonPath("$.data.userId").exists());
    }

    @Test
    void shouldListAllCompanies() throws Exception {
        when(companyService.getAllCompanies()).thenReturn(List.of(sampleCompanyResponse));

        mockMvc.perform(get("/api/companies")
                        .header("X-API-KEY", "company-service-secret-key-12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("66c25a1f2b3e8c0012345678"));
    }

    @Test
    void shouldGetCompanyById() throws Exception {
        when(companyService.getCompanyById("66c25a1f2b3e8c0012345678")).thenReturn(sampleCompanyResponse);

        mockMvc.perform(get("/api/companies/66c25a1f2b3e8c0012345678")
                        .header("X-API-KEY", "company-service-secret-key-12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("66c25a1f2b3e8c0012345678"));
    }

    @Test
    void shouldRejectRequestWithoutApiKeyOrUserContext() throws Exception {
        mockMvc.perform(get("/api/companies"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }
}
