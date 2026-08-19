package com.recruitment.company.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitment.company.config.SecurityConfig;
import com.recruitment.company.dto.CreateDepartmentRequest;
import com.recruitment.company.dto.DepartmentResponse;
import com.recruitment.company.security.ApiKeyFilter;
import com.recruitment.company.service.DepartmentService;
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

@WebMvcTest(DepartmentController.class)
@Import({SecurityConfig.class, ApiKeyFilter.class})
@ActiveProfiles("test")
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DepartmentService departmentService;

    private DepartmentResponse sampleDepartmentResponse;

    @BeforeEach
    void setUp() {
        sampleDepartmentResponse = DepartmentResponse.builder()
                .id("dept-1")
                .companyId("66c25a1f2b3e8c0012345678")
                .name("Engineering")
                .description("Core platform team")
                .headOfDepartment("Sarah Jenkins")
                .build();
    }

    @Test
    void shouldCreateDepartment() throws Exception {
        CreateDepartmentRequest request = CreateDepartmentRequest.builder()
                .name("Engineering")
                .description("Core platform team")
                .headOfDepartment("Sarah Jenkins")
                .build();

        when(departmentService.createDepartment(eq("66c25a1f2b3e8c0012345678"), any(CreateDepartmentRequest.class), any(), any()))
                .thenReturn(sampleDepartmentResponse);

        mockMvc.perform(post("/api/companies/66c25a1f2b3e8c0012345678/departments")
                        .header("X-API-KEY", "company-service-secret-key-12345")
                        .header("X-User-Id", "100")
                        .header("X-User-Role", "ROLE_COMPANY")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Engineering"));
    }

    @Test
    void shouldListDepartments() throws Exception {
        when(departmentService.getDepartmentsByCompanyId("66c25a1f2b3e8c0012345678"))
                .thenReturn(List.of(sampleDepartmentResponse));

        mockMvc.perform(get("/api/companies/66c25a1f2b3e8c0012345678/departments")
                        .header("X-API-KEY", "company-service-secret-key-12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("dept-1"));
    }
}
