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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecruiterServiceImpl implements RecruiterService {

    private final RecruiterRepository recruiterRepository;
    private final CompanyRepository companyRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public RecruiterResponse createRecruiter(String companyId, CreateRecruiterRequest request, Long authenticatedUserId, String role) {
        log.info("Creating recruiter '{}' for company ID: {}", request.getFullName(), companyId);

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        verifyOwnershipAndRole(company, authenticatedUserId, role);

        if (recruiterRepository.existsByCompanyIdAndEmailIgnoreCase(companyId, request.getEmail().trim())) {
            throw new DuplicateResourceException("Recruiter with email '" + request.getEmail() + "' already exists in this company");
        }

        // Validate department if specified
        if (request.getDepartmentId() != null && !request.getDepartmentId().isBlank()) {
            if (!departmentRepository.existsById(request.getDepartmentId())) {
                throw new ResourceNotFoundException("Department not found with id: " + request.getDepartmentId());
            }
        }

        Recruiter recruiter = Recruiter.builder()
                .companyId(companyId)
                .departmentId(request.getDepartmentId())
                .userId(request.getUserId())
                .fullName(request.getFullName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .phone(request.getPhone())
                .title(request.getTitle())
                .status(RecruiterStatus.ACTIVE)
                .build();

        Recruiter saved = recruiterRepository.save(recruiter);
        log.info("Recruiter created successfully with ID: {}", saved.getId());
        return RecruiterResponse.fromEntity(saved);
    }

    @Override
    public List<RecruiterResponse> getRecruitersByCompanyId(String companyId) {
        log.debug("Fetching all recruiters for company ID: {}", companyId);
        if (!companyRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company not found with id: " + companyId);
        }
        return recruiterRepository.findByCompanyId(companyId).stream()
                .map(RecruiterResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecruiterResponse> getRecruitersByDepartmentId(String companyId, String departmentId) {
        log.debug("Fetching recruiters for company ID: {}, department ID: {}", companyId, departmentId);
        if (!companyRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company not found with id: " + companyId);
        }
        return recruiterRepository.findByDepartmentId(departmentId).stream()
                .filter(r -> companyId.equals(r.getCompanyId()))
                .map(RecruiterResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public RecruiterResponse getRecruiterById(String companyId, String recruiterId) {
        log.debug("Fetching recruiter ID: {} in company ID: {}", recruiterId, companyId);
        Recruiter recruiter = recruiterRepository.findByIdAndCompanyId(recruiterId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found with id: " + recruiterId + " in company: " + companyId));
        return RecruiterResponse.fromEntity(recruiter);
    }

    @Override
    public RecruiterResponse updateRecruiter(String companyId, String recruiterId, UpdateRecruiterRequest request, Long authenticatedUserId, String role) {
        log.info("Updating recruiter ID: {} in company ID: {}", recruiterId, companyId);

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        verifyOwnershipAndRole(company, authenticatedUserId, role);

        Recruiter recruiter = recruiterRepository.findByIdAndCompanyId(recruiterId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found with id: " + recruiterId + " in company: " + companyId));

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            recruiter.setFullName(request.getFullName().trim());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String trimmedEmail = request.getEmail().trim().toLowerCase();
            if (!trimmedEmail.equalsIgnoreCase(recruiter.getEmail()) &&
                    recruiterRepository.existsByCompanyIdAndEmailIgnoreCase(companyId, trimmedEmail)) {
                throw new DuplicateResourceException("Recruiter with email '" + trimmedEmail + "' already exists in this company");
            }
            recruiter.setEmail(trimmedEmail);
        }

        if (request.getPhone() != null) {
            recruiter.setPhone(request.getPhone());
        }

        if (request.getTitle() != null) {
            recruiter.setTitle(request.getTitle());
        }

        if (request.getDepartmentId() != null) {
            if (!request.getDepartmentId().isBlank()) {
                if (!departmentRepository.existsById(request.getDepartmentId())) {
                    throw new ResourceNotFoundException("Department not found with id: " + request.getDepartmentId());
                }
                recruiter.setDepartmentId(request.getDepartmentId());
            } else {
                recruiter.setDepartmentId(null);
            }
        }

        if (request.getStatus() != null) {
            recruiter.setStatus(request.getStatus());
        }

        Recruiter updated = recruiterRepository.save(recruiter);
        log.info("Recruiter ID: {} updated successfully", updated.getId());
        return RecruiterResponse.fromEntity(updated);
    }

    @Override
    public void deleteRecruiter(String companyId, String recruiterId, Long authenticatedUserId, String role) {
        log.info("Deleting recruiter ID: {} in company ID: {}", recruiterId, companyId);

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        verifyOwnershipAndRole(company, authenticatedUserId, role);

        Recruiter recruiter = recruiterRepository.findByIdAndCompanyId(recruiterId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found with id: " + recruiterId + " in company: " + companyId));

        recruiterRepository.delete(recruiter);
        log.info("Recruiter ID: {} deleted successfully", recruiterId);
    }

    private void verifyOwnershipAndRole(Company company, Long authenticatedUserId, String role) {
        if (authenticatedUserId == null) {
            return;
        }

        if (role != null && (role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("ROLE_ADMIN"))) {
            return;
        }

        if (role != null && role.toUpperCase().contains("CANDIDATE")) {
            throw new UnauthorizedException("Candidates are not authorized to manage company recruiters");
        }

        if (!company.getUserId().equals(authenticatedUserId)) {
            log.warn("Ownership violation: User ID {} attempted to modify Recruiter in Company ID {} owned by User ID {}",
                    authenticatedUserId, company.getId(), company.getUserId());
            throw new UnauthorizedException("You are not authorized to modify recruiters for this company");
        }
    }
}
