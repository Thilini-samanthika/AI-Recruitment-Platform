package com.recruitment.company.service;

import com.recruitment.company.dto.CreateDepartmentRequest;
import com.recruitment.company.dto.DepartmentResponse;
import com.recruitment.company.dto.UpdateDepartmentRequest;
import com.recruitment.company.entity.Company;
import com.recruitment.company.entity.Department;
import com.recruitment.company.exception.DuplicateResourceException;
import com.recruitment.company.exception.ResourceNotFoundException;
import com.recruitment.company.exception.UnauthorizedException;
import com.recruitment.company.repository.CompanyRepository;
import com.recruitment.company.repository.DepartmentRepository;
import com.recruitment.company.repository.RecruiterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private RecruiterRepository recruiterRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Company sampleCompany;
    private Department sampleDepartment;

    @BeforeEach
    void setUp() {
        sampleCompany = Company.builder()
                .id("66c25a1f2b3e8c0012345678")
                .userId(100L)
                .companyName("Acme Technologies Inc.")
                .build();

        sampleDepartment = Department.builder()
                .id("dept-1")
                .companyId("66c25a1f2b3e8c0012345678")
                .name("Engineering")
                .description("Software and ML teams")
                .headOfDepartment("Sarah Jenkins")
                .build();
    }

    @Test
    void shouldCreateDepartmentSuccessfully() {
        CreateDepartmentRequest request = CreateDepartmentRequest.builder()
                .name("Engineering")
                .description("Software and ML teams")
                .headOfDepartment("Sarah Jenkins")
                .build();

        when(companyRepository.findById("66c25a1f2b3e8c0012345678")).thenReturn(Optional.of(sampleCompany));
        when(departmentRepository.existsByCompanyIdAndNameIgnoreCase("66c25a1f2b3e8c0012345678", "Engineering")).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenReturn(sampleDepartment);

        DepartmentResponse response = departmentService.createDepartment("66c25a1f2b3e8c0012345678", request, 100L, "ROLE_COMPANY");

        assertNotNull(response);
        assertEquals("dept-1", response.getId());
        assertEquals("Engineering", response.getName());
        assertEquals("Sarah Jenkins", response.getHeadOfDepartment());
    }

    @Test
    void shouldRejectDepartmentCreationForNonOwner() {
        CreateDepartmentRequest request = CreateDepartmentRequest.builder()
                .name("Engineering")
                .build();

        when(companyRepository.findById("66c25a1f2b3e8c0012345678")).thenReturn(Optional.of(sampleCompany));

        assertThrows(UnauthorizedException.class, () ->
                departmentService.createDepartment("66c25a1f2b3e8c0012345678", request, 999L, "ROLE_COMPANY"));
    }

    @Test
    void shouldThrowDuplicateExceptionForDuplicateDepartmentName() {
        CreateDepartmentRequest request = CreateDepartmentRequest.builder()
                .name("Engineering")
                .build();

        when(companyRepository.findById("66c25a1f2b3e8c0012345678")).thenReturn(Optional.of(sampleCompany));
        when(departmentRepository.existsByCompanyIdAndNameIgnoreCase("66c25a1f2b3e8c0012345678", "Engineering")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () ->
                departmentService.createDepartment("66c25a1f2b3e8c0012345678", request, 100L, "ROLE_COMPANY"));
    }

    @Test
    void shouldGetDepartmentsByCompanyId() {
        when(companyRepository.existsById("66c25a1f2b3e8c0012345678")).thenReturn(true);
        when(departmentRepository.findByCompanyId("66c25a1f2b3e8c0012345678")).thenReturn(List.of(sampleDepartment));

        List<DepartmentResponse> departments = departmentService.getDepartmentsByCompanyId("66c25a1f2b3e8c0012345678");

        assertNotNull(departments);
        assertEquals(1, departments.size());
        assertEquals("Engineering", departments.get(0).getName());
    }

    @Test
    void shouldUpdateDepartmentSuccessfully() {
        UpdateDepartmentRequest request = UpdateDepartmentRequest.builder()
                .name("Core Engineering")
                .build();

        when(companyRepository.findById("66c25a1f2b3e8c0012345678")).thenReturn(Optional.of(sampleCompany));
        when(departmentRepository.findByIdAndCompanyId("dept-1", "66c25a1f2b3e8c0012345678")).thenReturn(Optional.of(sampleDepartment));
        when(departmentRepository.existsByCompanyIdAndNameIgnoreCase("66c25a1f2b3e8c0012345678", "Core Engineering")).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenAnswer(i -> i.getArgument(0));

        DepartmentResponse response = departmentService.updateDepartment("66c25a1f2b3e8c0012345678", "dept-1", request, 100L, "ROLE_COMPANY");

        assertNotNull(response);
        assertEquals("Core Engineering", response.getName());
    }

    @Test
    void shouldDeleteDepartmentAndDetachRecruiters() {
        when(companyRepository.findById("66c25a1f2b3e8c0012345678")).thenReturn(Optional.of(sampleCompany));
        when(departmentRepository.findByIdAndCompanyId("dept-1", "66c25a1f2b3e8c0012345678")).thenReturn(Optional.of(sampleDepartment));
        when(recruiterRepository.findByDepartmentId("dept-1")).thenReturn(Collections.emptyList());
        doNothing().when(departmentRepository).delete(sampleDepartment);

        assertDoesNotThrow(() -> departmentService.deleteDepartment("66c25a1f2b3e8c0012345678", "dept-1", 100L, "ROLE_COMPANY"));
        verify(departmentRepository, times(1)).delete(sampleDepartment);
    }
}
