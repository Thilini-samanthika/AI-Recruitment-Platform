package com.recruitment.company.service;

import com.recruitment.company.dto.*;
import com.recruitment.company.entity.*;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private RecruiterRepository recruiterRepository;

    @InjectMocks
    private CompanyServiceImpl companyService;

    private Company sampleCompany;
    private CompanyProfile sampleProfile;

    @BeforeEach
    void setUp() {
        sampleProfile = CompanyProfile.builder()
                .industry("Information Technology")
                .companySize("51-200")
                .website("https://acmecorp.com")
                .description("Innovator in enterprise AI")
                .logoUrl("https://images.unsplash.com/photo-1549719386-74dfcbf7dbed")
                .build();

        sampleCompany = Company.builder()
                .id("66c25a1f2b3e8c0012345678")
                .userId(100L)
                .companyName("Acme Technologies Inc.")
                .email("contact@acmecorp.com")
                .phone("+1 (555) 234-5678")
                .address("100 Innovation Way, San Francisco, CA")
                .profile(sampleProfile)
                .verification(VerificationDetails.builder()
                        .status(VerificationStatus.UNVERIFIED)
                        .build())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldRegisterCompanySuccessfully() {
        CreateCompanyRequest request = CreateCompanyRequest.builder()
                .userId(100L)
                .companyName("Acme Technologies Inc.")
                .email("contact@acmecorp.com")
                .phone("+1 (555) 234-5678")
                .address("100 Innovation Way, San Francisco, CA")
                .build();

        when(companyRepository.existsByEmail("contact@acmecorp.com")).thenReturn(false);
        when(companyRepository.existsByUserId(100L)).thenReturn(false);
        when(companyRepository.save(any(Company.class))).thenReturn(sampleCompany);

        CompanyResponse response = companyService.registerCompany(request, 100L, "ROLE_COMPANY");

        assertNotNull(response);
        assertEquals("66c25a1f2b3e8c0012345678", response.getId());
        assertEquals("Acme Technologies Inc.", response.getCompanyName());
        assertEquals("contact@acmecorp.com", response.getEmail());
        assertEquals(100L, response.getUserId());
        verify(companyRepository, times(1)).save(any(Company.class));
    }

    @Test
    void shouldRejectCompanyRegistrationWhenRoleIsCandidate() {
        CreateCompanyRequest request = CreateCompanyRequest.builder()
                .userId(100L)
                .companyName("Acme Technologies Inc.")
                .email("contact@acmecorp.com")
                .build();

        assertThrows(UnauthorizedException.class, () ->
                companyService.registerCompany(request, 100L, "ROLE_CANDIDATE"));
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void shouldThrowDuplicateExceptionWhenEmailAlreadyExists() {
        CreateCompanyRequest request = CreateCompanyRequest.builder()
                .userId(100L)
                .companyName("Acme Technologies Inc.")
                .email("contact@acmecorp.com")
                .build();

        when(companyRepository.existsByEmail("contact@acmecorp.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> companyService.registerCompany(request, 100L, "ROLE_COMPANY"));
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void shouldThrowDuplicateExceptionWhenUserIdAlreadyHasCompany() {
        CreateCompanyRequest request = CreateCompanyRequest.builder()
                .userId(100L)
                .companyName("Acme Technologies Inc.")
                .email("contact@acmecorp.com")
                .build();

        when(companyRepository.existsByEmail("contact@acmecorp.com")).thenReturn(false);
        when(companyRepository.existsByUserId(100L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> companyService.registerCompany(request, 100L, "ROLE_COMPANY"));
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void shouldGetAllCompanies() {
        when(companyRepository.findAll()).thenReturn(List.of(sampleCompany));

        List<CompanyResponse> list = companyService.getAllCompanies();

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("Acme Technologies Inc.", list.get(0).getCompanyName());
    }

    @Test
    void shouldGetCompanyById() {
        when(companyRepository.findById("66c25a1f2b3e8c0012345678")).thenReturn(Optional.of(sampleCompany));

        CompanyResponse response = companyService.getCompanyById("66c25a1f2b3e8c0012345678");

        assertNotNull(response);
        assertEquals("66c25a1f2b3e8c0012345678", response.getId());
        assertEquals("Acme Technologies Inc.", response.getCompanyName());
    }

    @Test
    void shouldThrowNotFoundWhenCompanyDoesNotExist() {
        when(companyRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> companyService.getCompanyById("non-existent-id"));
    }

    @Test
    void shouldGetCompanyByUserId() {
        when(companyRepository.findByUserId(100L)).thenReturn(Optional.of(sampleCompany));

        CompanyResponse response = companyService.getCompanyByUserId(100L);

        assertNotNull(response);
        assertEquals(100L, response.getUserId());
        assertEquals("Acme Technologies Inc.", response.getCompanyName());
    }

    @Test
    void shouldUpdateCompanySuccessfullyWhenAuthorized() {
        UpdateCompanyRequest updateReq = UpdateCompanyRequest.builder()
                .companyName("Acme Global Tech")
                .phone("+1 (555) 999-8888")
                .address("200 Tech Blvd, San Jose, CA")
                .build();

        when(companyRepository.findById("66c25a1f2b3e8c0012345678")).thenReturn(Optional.of(sampleCompany));
        when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompanyResponse response = companyService.updateCompany("66c25a1f2b3e8c0012345678", updateReq, 100L, "ROLE_COMPANY");

        assertNotNull(response);
        assertEquals("Acme Global Tech", response.getCompanyName());
        assertEquals("+1 (555) 999-8888", response.getPhone());
    }

    @Test
    void shouldThrowUnauthorizedWhenModifyingOtherUsersCompany() {
        UpdateCompanyRequest updateReq = UpdateCompanyRequest.builder()
                .companyName("Acme Global Tech")
                .build();

        when(companyRepository.findById("66c25a1f2b3e8c0012345678")).thenReturn(Optional.of(sampleCompany));

        assertThrows(UnauthorizedException.class, () ->
                companyService.updateCompany("66c25a1f2b3e8c0012345678", updateReq, 999L, "ROLE_COMPANY"));
    }

    @Test
    void shouldAllowAdminToUpdateAnyCompany() {
        UpdateCompanyRequest updateReq = UpdateCompanyRequest.builder()
                .companyName("Acme Admin Override")
                .build();

        when(companyRepository.findById("66c25a1f2b3e8c0012345678")).thenReturn(Optional.of(sampleCompany));
        when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompanyResponse response = companyService.updateCompany("66c25a1f2b3e8c0012345678", updateReq, 999L, "ROLE_ADMIN");

        assertNotNull(response);
        assertEquals("Acme Admin Override", response.getCompanyName());
    }

    @Test
    void shouldSaveOrUpdateProfileSuccessfully() {
        CompanyProfileRequest profileReq = CompanyProfileRequest.builder()
                .industry("Fintech")
                .companySize("201-500")
                .website("https://acmefintech.io")
                .description("Next-gen payment processing")
                .logoUrl("https://images.unsplash.com/photo-1549719386-74dfcbf7dbed")
                .build();

        when(companyRepository.findById("66c25a1f2b3e8c0012345678")).thenReturn(Optional.of(sampleCompany));
        when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompanyProfileResponse response = companyService.saveOrUpdateProfile("66c25a1f2b3e8c0012345678", profileReq, 100L, "ROLE_COMPANY");

        assertNotNull(response);
        assertEquals("Fintech", response.getIndustry());
        assertEquals("201-500", response.getCompanySize());
        assertEquals("https://acmefintech.io", response.getWebsite());
    }

    @Test
    void shouldSubmitVerificationSuccessfully() {
        SubmitVerificationRequest req = SubmitVerificationRequest.builder()
                .taxId("TAX-11223344")
                .businessRegistrationNumber("REG-CA-2026-99")
                .documentUrl("https://docs.recruitment.com/acme.pdf")
                .build();

        when(companyRepository.findById("66c25a1f2b3e8c0012345678")).thenReturn(Optional.of(sampleCompany));
        when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VerificationResponse response = companyService.submitVerification("66c25a1f2b3e8c0012345678", req, 100L, "ROLE_COMPANY");

        assertNotNull(response);
        assertEquals(VerificationStatus.PENDING, response.getStatus());
        assertEquals("TAX-11223344", response.getTaxId());
        assertEquals("REG-CA-2026-99", response.getBusinessRegistrationNumber());
    }

    @Test
    void shouldReviewVerificationByAdmin() {
        ReviewVerificationRequest reviewReq = ReviewVerificationRequest.builder()
                .status(VerificationStatus.VERIFIED)
                .reviewNotes("All incorporation records confirmed.")
                .build();

        when(companyRepository.findById("66c25a1f2b3e8c0012345678")).thenReturn(Optional.of(sampleCompany));
        when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VerificationResponse response = companyService.reviewVerification("66c25a1f2b3e8c0012345678", reviewReq, "ROLE_ADMIN", "ADMIN_1");

        assertNotNull(response);
        assertEquals(VerificationStatus.VERIFIED, response.getStatus());
        assertEquals("All incorporation records confirmed.", response.getReviewNotes());
        assertEquals("ADMIN_1", response.getReviewedBy());
    }

    @Test
    void shouldRejectVerificationReviewByNonAdmin() {
        ReviewVerificationRequest reviewReq = ReviewVerificationRequest.builder()
                .status(VerificationStatus.VERIFIED)
                .build();

        assertThrows(UnauthorizedException.class, () ->
                companyService.reviewVerification("66c25a1f2b3e8c0012345678", reviewReq, "ROLE_COMPANY", "100"));
    }

    @Test
    void shouldDeleteCompanyAndCascadeToDepartmentsAndRecruiters() {
        when(companyRepository.findById("66c25a1f2b3e8c0012345678")).thenReturn(Optional.of(sampleCompany));
        doNothing().when(departmentRepository).deleteByCompanyId("66c25a1f2b3e8c0012345678");
        doNothing().when(recruiterRepository).deleteByCompanyId("66c25a1f2b3e8c0012345678");
        doNothing().when(companyRepository).delete(sampleCompany);

        assertDoesNotThrow(() -> companyService.deleteCompany("66c25a1f2b3e8c0012345678", 100L, "ROLE_COMPANY"));
        verify(departmentRepository, times(1)).deleteByCompanyId("66c25a1f2b3e8c0012345678");
        verify(recruiterRepository, times(1)).deleteByCompanyId("66c25a1f2b3e8c0012345678");
        verify(companyRepository, times(1)).delete(sampleCompany);
    }
}
