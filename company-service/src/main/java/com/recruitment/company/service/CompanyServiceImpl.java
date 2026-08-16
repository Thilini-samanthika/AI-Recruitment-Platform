package com.recruitment.company.service;

import com.recruitment.company.dto.*;
import com.recruitment.company.entity.Company;
import com.recruitment.company.entity.CompanyProfile;
import com.recruitment.company.exception.DuplicateResourceException;
import com.recruitment.company.exception.ResourceNotFoundException;
import com.recruitment.company.exception.UnauthorizedException;
import com.recruitment.company.repository.CompanyProfileRepository;
import com.recruitment.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyProfileRepository companyProfileRepository;

    @Override
    @Transactional
    public CompanyResponse registerCompany(CreateCompanyRequest request) {
        log.info("Registering company for user ID: {}, email: {}", request.getUserId(), request.getEmail());

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
                .build();

        Company savedCompany = companyRepository.save(company);
        log.info("Company registered successfully with ID: {}", savedCompany.getId());
        return CompanyResponse.fromEntity(savedCompany);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyResponse> getAllCompanies() {
        log.debug("Fetching all registered companies");
        return companyRepository.findAll().stream()
                .map(CompanyResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyResponse getCompanyById(Long id) {
        log.debug("Fetching company by ID: {}", id);
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
        return CompanyResponse.fromEntity(company);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyResponse getCompanyByUserId(Long userId) {
        log.debug("Fetching company by User ID: {}", userId);
        Company company = companyRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found for user ID: " + userId));
        return CompanyResponse.fromEntity(company);
    }

    @Override
    @Transactional
    public CompanyResponse updateCompany(Long id, UpdateCompanyRequest request, Long authenticatedUserId, String role) {
        log.info("Updating company with ID: {}", id);
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));

        verifyOwnership(company, authenticatedUserId, role);

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
    @Transactional
    public void deleteCompany(Long id, Long authenticatedUserId, String role) {
        log.info("Deleting company with ID: {}", id);
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));

        verifyOwnership(company, authenticatedUserId, role);

        companyRepository.delete(company);
        log.info("Company ID: {} deleted successfully", id);
    }

    @Override
    @Transactional
    public CompanyProfileResponse saveOrUpdateProfile(Long companyId, CompanyProfileRequest request, Long authenticatedUserId, String role) {
        log.info("Saving/updating extended profile for company ID: {}", companyId);
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        verifyOwnership(company, authenticatedUserId, role);

        CompanyProfile profile = company.getProfile();
        if (profile == null) {
            profile = CompanyProfile.builder()
                    .company(company)
                    .industry(request.getIndustry())
                    .companySize(request.getCompanySize())
                    .website(request.getWebsite())
                    .description(request.getDescription())
                    .logoUrl(request.getLogoUrl())
                    .build();
            company.setProfile(profile);
        } else {
            if (request.getIndustry() != null) profile.setIndustry(request.getIndustry());
            if (request.getCompanySize() != null) profile.setCompanySize(request.getCompanySize());
            if (request.getWebsite() != null) profile.setWebsite(request.getWebsite());
            if (request.getDescription() != null) profile.setDescription(request.getDescription());
            if (request.getLogoUrl() != null) profile.setLogoUrl(request.getLogoUrl());
        }

        CompanyProfile savedProfile = companyProfileRepository.save(profile);
        log.info("Company profile for company ID: {} saved successfully", companyId);
        return CompanyProfileResponse.fromEntity(savedProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyProfileResponse getCompanyProfile(Long companyId) {
        log.debug("Fetching profile for company ID: {}", companyId);
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        CompanyProfile profile = company.getProfile();
        if (profile == null) {
            throw new ResourceNotFoundException("Company profile not found for company ID: " + companyId);
        }

        return CompanyProfileResponse.fromEntity(profile);
    }

    /**
     * Verifies that the authenticated user owns the company or has administrative rights.
     */
    private void verifyOwnership(Company company, Long authenticatedUserId, String role) {
        if (authenticatedUserId == null) {
            // Internal service invocation with valid API key
            return;
        }

        if (role != null && (role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("ROLE_ADMIN"))) {
            return;
        }

        if (!company.getUserId().equals(authenticatedUserId)) {
            log.warn("Ownership violation: User ID {} attempted to modify Company ID {} owned by User ID {}",
                    authenticatedUserId, company.getId(), company.getUserId());
            throw new UnauthorizedException("You are not authorized to modify or delete this company resource");
        }
    }
}
