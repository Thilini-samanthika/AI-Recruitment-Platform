package com.recruitment.company.service;

import com.recruitment.company.dto.CreateRecruiterRequest;
import com.recruitment.company.dto.RecruiterResponse;
import com.recruitment.company.dto.UpdateRecruiterRequest;
import com.recruitment.company.entity.Company;
import com.recruitment.company.entity.Recruiter;
import com.recruitment.company.entity.RecruiterStatus;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecruiterServiceTest {

    @Mock
    private RecruiterRepository recruiterRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private RecruiterServiceImpl recruiterService;

    private Company sampleCompany;
    private Recruiter sampleRecruiter;

    @BeforeEach
    void setUp() {
        sampleCompany = Company.builder()
                .id("66c25a1f2b3e8c0012345678")
                .userId(100L)
                .companyName("Acme Technologies Inc.")
                .build();

        sampleRecruiter = Recruiter.builder()
                .id("recruiter-1")
                .companyId("66c25a1f2b3e8c0012345678")
                .departmentId("dept-1")
                .userId(105L)
                .fullName("Jane Doe")
                .email("jane.doe@acmetech.io")
                .phone("+1 (555) 345-6789")
                .title("Senior Recruiter")
                .status(RecruiterStatus.ACTIVE)
                .build();
    }

    @Test
    void shouldCreateRecruiterSuccessfully() {
        CreateRecruiterRequest request = CreateRecruiterRequest.builder()
                .fullName("Jane Doe")
                .email("jane.doe@acmetech.io")
                .phone("+1 (555) 345-6789")
                .title("Senior Recruiter")
                .departmentId("dept-1")
                .userId(105L)
                .build();

        when(companyRepository.findById("66c25a1f2b3e8c0012345678")).thenReturn(Optional.of(sampleCompany));
        when(recruiterRepository.existsByCompanyIdAndEmailIgnoreCase("66c25a1f2b3e8c0012345678", "jane.doe@acmetech.io")).thenReturn(false);
        when(departmentRepository.existsById("dept-1")).thenReturn(true);
        when(recruiterRepository.save(any(Recruiter.class))).thenReturn(sampleRecruiter);

        RecruiterResponse response = recruiterService.createRecruiter("66c25a1f2b3e8c0012345678", request, 100L, "ROLE_COMPANY");

        assertNotNull(response);
        assertEquals("recruiter-1", response.getId());
        assertEquals("Jane Doe", response.getFullName());
        assertEquals("jane.doe@acmetech.io", response.getEmail());
        assertEquals(RecruiterStatus.ACTIVE, response.getStatus());
    }

    @Test
    void shouldThrowDuplicateExceptionWhenRecruiterEmailExistsInCompany() {
        CreateRecruiterRequest request = CreateRecruiterRequest.builder()
                .fullName("Jane Doe")
                .email("jane.doe@acmetech.io")
                .build();

        when(companyRepository.findById("66c25a1f2b3e8c0012345678")).thenReturn(Optional.of(sampleCompany));
        when(recruiterRepository.existsByCompanyIdAndEmailIgnoreCase("66c25a1f2b3e8c0012345678", "jane.doe@acmetech.io")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () ->
                recruiterService.createRecruiter("66c25a1f2b3e8c0012345678", request, 100L, "ROLE_COMPANY"));
    }

    @Test
    void shouldRejectRecruiterCreationWhenUserDoesNotOwnCompany() {
        CreateRecruiterRequest request = CreateRecruiterRequest.builder()
                .fullName("Jane Doe")
                .email("jane.doe@acmetech.io")
                .build();

        when(companyRepository.findById("66c25a1f2b3e8c0012345678")).thenReturn(Optional.of(sampleCompany));

        assertThrows(UnauthorizedException.class, () ->
                recruiterService.createRecruiter("66c25a1f2b3e8c0012345678", request, 999L, "ROLE_COMPANY"));
    }

    @Test
    void shouldGetRecruitersByCompanyId() {
        when(companyRepository.existsById("66c25a1f2b3e8c0012345678")).thenReturn(true);
        when(recruiterRepository.findByCompanyId("66c25a1f2b3e8c0012345678")).thenReturn(List.of(sampleRecruiter));

        List<RecruiterResponse> list = recruiterService.getRecruitersByCompanyId("66c25a1f2b3e8c0012345678");

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("Jane Doe", list.get(0).getFullName());
    }

    @Test
    void shouldUpdateRecruiterSuccessfully() {
        UpdateRecruiterRequest request = UpdateRecruiterRequest.builder()
                .fullName("Jane Doe-Smith")
                .status(RecruiterStatus.INACTIVE)
                .build();

        when(companyRepository.findById("66c25a1f2b3e8c0012345678")).thenReturn(Optional.of(sampleCompany));
        when(recruiterRepository.findByIdAndCompanyId("recruiter-1", "66c25a1f2b3e8c0012345678")).thenReturn(Optional.of(sampleRecruiter));
        when(recruiterRepository.save(any(Recruiter.class))).thenAnswer(i -> i.getArgument(0));

        RecruiterResponse response = recruiterService.updateRecruiter("66c25a1f2b3e8c0012345678", "recruiter-1", request, 100L, "ROLE_COMPANY");

        assertNotNull(response);
        assertEquals("Jane Doe-Smith", response.getFullName());
        assertEquals(RecruiterStatus.INACTIVE, response.getStatus());
    }

    @Test
    void shouldDeleteRecruiterSuccessfully() {
        when(companyRepository.findById("66c25a1f2b3e8c0012345678")).thenReturn(Optional.of(sampleCompany));
        when(recruiterRepository.findByIdAndCompanyId("recruiter-1", "66c25a1f2b3e8c0012345678")).thenReturn(Optional.of(sampleRecruiter));
        doNothing().when(recruiterRepository).delete(sampleRecruiter);

        assertDoesNotThrow(() -> recruiterService.deleteRecruiter("66c25a1f2b3e8c0012345678", "recruiter-1", 100L, "ROLE_COMPANY"));
        verify(recruiterRepository, times(1)).delete(sampleRecruiter);
    }
}
