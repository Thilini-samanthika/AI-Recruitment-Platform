package com.recruitment.company.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitment.company.dto.*;
import com.recruitment.company.service.CompanyService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
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
                .id(1L)
                .userId(100L)
                .companyName("Acme Technologies Inc.")
                .email("contact@acmecorp.com")
                .phone("+1 (555) 234-5678")
                .address("100 Innovation Way, San Francisco, CA")
                .profile(CompanyProfileResponse.builder()
                        .id(1L)
                        .companyId(1L)
                        .industry("Information Technology")
                        .companySize("51-200")
                        .website("https://acmecorp.com")
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

        when(companyService.registerCompany(any(CreateCompanyRequest.class))).thenReturn(sampleCompanyResponse);

        mockMvc.perform(post("/api/companies")
                        .header("X-API-KEY", "company-service-secret-key-12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.companyName").value("Acme Technologies Inc."));
    }

    @Test
    void shouldListAllCompanies() throws Exception {
        when(companyService.getAllCompanies()).thenReturn(List.of(sampleCompanyResponse));

        mockMvc.perform(get("/api/companies")
                        .header("X-API-KEY", "company-service-secret-key-12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1L));
    }

    @Test
    void shouldGetCompanyById() throws Exception {
        when(companyService.getCompanyById(1L)).thenReturn(sampleCompanyResponse);

        mockMvc.perform(get("/api/companies/1")
                        .header("X-API-KEY", "company-service-secret-key-12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    void shouldRejectRequestWithoutApiKeyOrUserContext() throws Exception {
        mockMvc.perform(get("/api/companies"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }
}
