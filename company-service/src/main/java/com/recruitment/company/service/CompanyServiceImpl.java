package com.recruitment.company.service;

import com.recruitment.company.dto.*;
import com.recruitment.company.entity.*;
import com.recruitment.company.exception.DuplicateResourceException;
import com.recruitment.company.exception.ResourceNotFoundException;
import com.recruitment.company.exception.UnauthorizedException;
import com.recruitment.company.repository.CompanyRepository;
import com.recruitment.company.repository.DepartmentRepository;
import com.recruitment.company.repository.RecruiterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final DepartmentRepository departmentRepository;
    private final RecruiterRepository recruiterRepository;

    @Override
    public CompanyResponse registerCompany(CreateCompanyRequest request, Long authenticatedUserId, String role) {
        log.info("Registering company for user ID: {}, email: {}", request.getUserId(), request.getEmail());

        // Role authorization check for registration
        if (role != null) {
            String normalizedRole = role.toUpperCase();
            if (normalizedRole.contains("CANDIDATE")) {
                log.warn("Candidate user (ID: {}) attempted to register a company", authenticatedUserId);
                throw new UnauthorizedException("Candidates are not authorized to create company accounts. Required role: ROLE_COMPANY or ROLE_ADMIN");
            }
        }

        // Verify authenticated user matches payload user ID (unless admin)
        if (authenticatedUserId != null && !isAdmin(role) && !authenticatedUserId.equals(request.getUserId())) {
            log.warn("Authenticated user {} attempted to register company for user ID {}", authenticatedUserId, request.getUserId());
            throw new UnauthorizedException("Authenticated user ID does not match the company owner user ID");
        }

        if (companyRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Company with email '" + request.getEmail() + "' already exists");
        }

        if (companyRepository.existsByUserId(request.getUserId())) {
            throw new DuplicateResourceException("User ID " + request.getUserId() + " already has a registered company");
        }

        Company company = Company.builder()
                .userId(request.getUserId())
                .companyName(request.getCompanyName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .phone(request.getPhone())
                .address(request.getAddress())
                .verification(VerificationDetails.builder()
                        .status(VerificationStatus.UNVERIFIED)
                        .build())
                .build();

        Company savedCompany = companyRepository.save(company);
        log.info("Company registered successfully with ID: {}", savedCompany.getId());
        return CompanyResponse.fromEntity(savedCompany);
    }

    @Override
    public List<CompanyResponse> getAllCompanies() {
        log.debug("Fetching all registered companies");
        return companyRepository.findAll().stream()
                .map(CompanyResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public CompanyResponse getCompanyById(String id) {
        log.debug("Fetching company by ID: {}", id);
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
        return CompanyResponse.fromEntity(company);
    }

    @Override
    public CompanyResponse getCompanyByUserId(Long userId) {
        log.debug("Fetching company by User ID: {}", userId);
        Company company = companyRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found for user ID: " + userId));
        return CompanyResponse.fromEntity(company);
    }

    @Override
    public CompanyResponse updateCompany(String id, UpdateCompanyRequest request, Long authenticatedUserId, String role) {
        log.info("Updating company with ID: {}", id);
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));

        verifyOwnershipAndRole(company, authenticatedUserId, role);

        if (request.getCompanyName() != null && !request.getCompanyName().isBlank()) {
            company.setCompanyName(request.getCompanyName().trim());
        }
        if (request.getPhone() != null) {
            company.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            company.setAddress(request.getAddress());
        }

        Company updatedCompany = companyRepository.save(company);
        log.info("Company ID: {} updated successfully", updatedCompany.getId());
        return CompanyResponse.fromEntity(updatedCompany);
    }

    @Override
    public void deleteCompany(String id, Long authenticatedUserId, String role) {
        log.info("Deleting company with ID: {}", id);
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));

        verifyOwnershipAndRole(company, authenticatedUserId, role);

        // Cascade delete child entities
        departmentRepository.deleteByCompanyId(id);
        recruiterRepository.deleteByCompanyId(id);
        companyRepository.delete(company);
        log.info("Company ID: {} and its related departments/recruiters deleted successfully", id);
    }

    @Override
    public CompanyProfileResponse saveOrUpdateProfile(String companyId, CompanyProfileRequest request, Long authenticatedUserId, String role) {
        log.info("Saving/updating embedded profile for company ID: {}", companyId);
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        verifyOwnershipAndRole(company, authenticatedUserId, role);

        CompanyProfile profile = company.getProfile();
        if (profile == null) {
            profile = CompanyProfile.builder()
                    .industry(request.getIndustry())
                    .companySize(request.getCompanySize())
                    .website(request.getWebsite())
                    .description(request.getDescription())
                    .logoUrl(request.getLogoUrl())
                    .build();
        } else {
            if (request.getIndustry() != null) profile.setIndustry(request.getIndustry());
            if (request.getCompanySize() != null) profile.setCompanySize(request.getCompanySize());
            if (request.getWebsite() != null) profile.setWebsite(request.getWebsite());
            if (request.getDescription() != null) profile.setDescription(request.getDescription());
            if (request.getLogoUrl() != null) profile.setLogoUrl(request.getLogoUrl());
        }

        company.setProfile(profile);
        companyRepository.save(company);
        log.info("Company profile for company ID: {} saved successfully", companyId);
        return CompanyProfileResponse.fromEntity(profile);
    }

    @Override
    public CompanyProfileResponse getCompanyProfile(String companyId) {
        log.debug("Fetching profile for company ID: {}", companyId);
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        CompanyProfile profile = company.getProfile();
        if (profile == null) {
            throw new ResourceNotFoundException("Company profile not found for company ID: " + companyId);
        }

        return CompanyProfileResponse.fromEntity(profile);
    }

    @Override
    public VerificationResponse submitVerification(String companyId, SubmitVerificationRequest request, Long authenticatedUserId, String role) {
        log.info("Submitting verification request for company ID: {}", companyId);
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        verifyOwnershipAndRole(company, authenticatedUserId, role);

        VerificationDetails verification = VerificationDetails.builder()
                .status(VerificationStatus.PENDING)
                .taxId(request.getTaxId().trim())
                .businessRegistrationNumber(request.getBusinessRegistrationNumber().trim())
                .documentUrl(request.getDocumentUrl().trim())
                .submittedAt(LocalDateTime.now())
                .build();

        company.setVerification(verification);
        Company updated = companyRepository.save(company);
        log.info("Verification submitted for company ID: {}, status set to PENDING", companyId);
        return VerificationResponse.fromEntity(updated.getVerification());
    }

    @Override
    public VerificationResponse reviewVerification(String companyId, ReviewVerificationRequest request, String adminRole, String adminUserId) {
        log.info("Admin review for company ID: {}, status: {}", companyId, request.getStatus());

        if (!isAdmin(adminRole)) {
            log.warn("Non-admin user attempted to review company verification: role={}", adminRole);
            throw new UnauthorizedException("Access Denied: Only ROLE_ADMIN can review company verification requests");
        }

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        VerificationDetails verification = company.getVerification();
        if (verification == null) {
            verification = new VerificationDetails();
        }

        verification.setStatus(request.getStatus());
        verification.setReviewNotes(request.getReviewNotes());
        verification.setReviewedAt(LocalDateTime.now());
        verification.setReviewedBy(adminUserId != null ? adminUserId : "ADMIN");

        company.setVerification(verification);
        Company updated = companyRepository.save(company);
        log.info("Verification status updated for company ID: {} to {}", companyId, request.getStatus());
        return VerificationResponse.fromEntity(updated.getVerification());
    }

    @Override
    public VerificationResponse getVerificationStatus(String companyId, Long authenticatedUserId, String role) {
        log.debug("Fetching verification status for company ID: {}", companyId);
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        // Owner, Admin, or internal service can view verification details
        verifyOwnershipAndRole(company, authenticatedUserId, role);

        return VerificationResponse.fromEntity(company.getVerification());
    }

    /**
     * Verifies that the caller has appropriate role and ownership rights over the company resource.
     */
    private void verifyOwnershipAndRole(Company company, Long authenticatedUserId, String role) {
        if (authenticatedUserId == null) {
            // Internal microservice invocation with valid X-API-KEY
            return;
        }

        if (isAdmin(role)) {
            return;
        }

        if (role != null) {
            String normalizedRole = role.toUpperCase();
            if (normalizedRole.contains("CANDIDATE")) {
                log.warn("Candidate user (ID: {}) attempted corporate modification on company {}", authenticatedUserId, company.getId());
                throw new UnauthorizedException("Candidates are not authorized to modify corporate resources");
            }
        }

        if (!company.getUserId().equals(authenticatedUserId)) {
            log.warn("Ownership violation: User ID {} attempted to modify Company ID {} owned by User ID {}",
                    authenticatedUserId, company.getId(), company.getUserId());
            throw new UnauthorizedException("You are not authorized to modify or delete this company resource");
        }
    }

    private boolean isAdmin(String role) {
        return role != null && (role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("ROLE_ADMIN"));
    }
}
