package com.recruitment.company.service;

import com.recruitment.company.dto.*;
import com.recruitment.company.entity.Company;
import com.recruitment.company.entity.CompanyProfile;
import com.recruitment.company.exception.DuplicateResourceException;
import com.recruitment.company.exception.ResourceNotFoundException;
import com.recruitment.company.exception.UnauthorizedException;
import com.recruitment.company.repository.CompanyProfileRepository;
import com.recruitment.company.repository.CompanyRepository;
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
    private CompanyProfileRepository companyProfileRepository;

    @InjectMocks
    private CompanyServiceImpl companyService;

    private Company sampleCompany;
    private CompanyProfile sampleProfile;

    @BeforeEach
    void setUp() {
        sampleProfile = CompanyProfile.builder()
                .id(1L)
                .industry("Information Technology")
                .companySize("51-200")
                .website("https://acmecorp.com")
                .description("Innovator in enterprise AI")
                .logoUrl("https://images.unsplash.com/photo-1549719386-74dfcbf7dbed")
                .build();

        sampleCompany = Company.builder()
                .id(1L)
                .userId(100L)
                .companyName("Acme Technologies Inc.")
                .email("contact@acmecorp.com")
                .phone("+1 (555) 234-5678")
                .address("100 Innovation Way, San Francisco, CA")
                .profile(sampleProfile)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleProfile.setCompany(sampleCompany);
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

        CompanyResponse response = companyService.registerCompany(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Acme Technologies Inc.", response.getCompanyName());
        assertEquals("contact@acmecorp.com", response.getEmail());
        assertEquals(100L, response.getUserId());
        verify(companyRepository, times(1)).save(any(Company.class));
    }

    @Test
    void shouldThrowDuplicateExceptionWhenEmailAlreadyExists() {
        CreateCompanyRequest request = CreateCompanyRequest.builder()
                .userId(100L)
                .companyName("Acme Technologies Inc.")
                .email("contact@acmecorp.com")
                .build();

        when(companyRepository.existsByEmail("contact@acmecorp.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> companyService.registerCompany(request));
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

        assertThrows(DuplicateResourceException.class, () -> companyService.registerCompany(request));
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
        when(companyRepository.findById(1L)).thenReturn(Optional.of(sampleCompany));

        CompanyResponse response = companyService.getCompanyById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Acme Technologies Inc.", response.getCompanyName());
    }

    @Test
    void shouldThrowNotFoundWhenCompanyDoesNotExist() {
        when(companyRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> companyService.getCompanyById(999L));
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

        when(companyRepository.findById(1L)).thenReturn(Optional.of(sampleCompany));
        when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompanyResponse response = companyService.updateCompany(1L, updateReq, 100L, "ROLE_COMPANY");

        assertNotNull(response);
        assertEquals("Acme Global Tech", response.getCompanyName());
        assertEquals("+1 (555) 999-8888", response.getPhone());
    }

    @Test
    void shouldThrowUnauthorizedWhenModifyingOtherUsersCompany() {
        UpdateCompanyRequest updateReq = UpdateCompanyRequest.builder()
                .companyName("Acme Global Tech")
                .build();

        when(companyRepository.findById(1L)).thenReturn(Optional.of(sampleCompany));

        assertThrows(UnauthorizedException.class, () ->
                companyService.updateCompany(1L, updateReq, 999L, "ROLE_COMPANY"));
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

        when(companyRepository.findById(1L)).thenReturn(Optional.of(sampleCompany));
        when(companyProfileRepository.save(any(CompanyProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompanyProfileResponse response = companyService.saveOrUpdateProfile(1L, profileReq, 100L, "ROLE_COMPANY");

        assertNotNull(response);
        assertEquals("Fintech", response.getIndustry());
        assertEquals("201-500", response.getCompanySize());
        assertEquals("https://acmefintech.io", response.getWebsite());
    }

    @Test
    void shouldDeleteCompanySuccessfullyWhenAuthorized() {
        when(companyRepository.findById(1L)).thenReturn(Optional.of(sampleCompany));
        doNothing().when(companyRepository).delete(sampleCompany);

        assertDoesNotThrow(() -> companyService.deleteCompany(1L, 100L, "ROLE_COMPANY"));
        verify(companyRepository, times(1)).delete(sampleCompany);
    }
}
