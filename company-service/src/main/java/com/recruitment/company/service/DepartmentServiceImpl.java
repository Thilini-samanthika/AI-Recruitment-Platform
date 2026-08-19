package com.recruitment.company.service;

import com.recruitment.company.dto.CreateDepartmentRequest;
import com.recruitment.company.dto.DepartmentResponse;
import com.recruitment.company.dto.UpdateDepartmentRequest;
import com.recruitment.company.entity.Company;
import com.recruitment.company.entity.Department;
import com.recruitment.company.entity.Recruiter;
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
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final CompanyRepository companyRepository;
    private final RecruiterRepository recruiterRepository;

    @Override
    public DepartmentResponse createDepartment(String companyId, CreateDepartmentRequest request, Long authenticatedUserId, String role) {
        log.info("Creating department '{}' for company ID: {}", request.getName(), companyId);

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        verifyOwnershipAndRole(company, authenticatedUserId, role);

        if (departmentRepository.existsByCompanyIdAndNameIgnoreCase(companyId, request.getName().trim())) {
            throw new DuplicateResourceException("Department with name '" + request.getName() + "' already exists for this company");
        }

        Department department = Department.builder()
                .companyId(companyId)
                .name(request.getName().trim())
                .description(request.getDescription())
                .headOfDepartment(request.getHeadOfDepartment())
                .build();

        Department saved = departmentRepository.save(department);
        log.info("Department created successfully with ID: {}", saved.getId());
        return DepartmentResponse.fromEntity(saved);
    }

    @Override
    public List<DepartmentResponse> getDepartmentsByCompanyId(String companyId) {
        log.debug("Fetching all departments for company ID: {}", companyId);
        if (!companyRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company not found with id: " + companyId);
        }
        return departmentRepository.findByCompanyId(companyId).stream()
                .map(DepartmentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentResponse getDepartmentById(String companyId, String departmentId) {
        log.debug("Fetching department ID: {} for company ID: {}", departmentId, companyId);
        Department department = departmentRepository.findByIdAndCompanyId(departmentId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId + " in company: " + companyId));
        return DepartmentResponse.fromEntity(department);
    }

    @Override
    public DepartmentResponse updateDepartment(String companyId, String departmentId, UpdateDepartmentRequest request, Long authenticatedUserId, String role) {
        log.info("Updating department ID: {} in company ID: {}", departmentId, companyId);

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        verifyOwnershipAndRole(company, authenticatedUserId, role);

        Department department = departmentRepository.findByIdAndCompanyId(departmentId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId + " in company: " + companyId));

        if (request.getName() != null && !request.getName().isBlank()) {
            String trimmedName = request.getName().trim();
            if (!trimmedName.equalsIgnoreCase(department.getName()) &&
                    departmentRepository.existsByCompanyIdAndNameIgnoreCase(companyId, trimmedName)) {
                throw new DuplicateResourceException("Department with name '" + trimmedName + "' already exists for this company");
            }
            department.setName(trimmedName);
        }

        if (request.getDescription() != null) {
            department.setDescription(request.getDescription());
        }

        if (request.getHeadOfDepartment() != null) {
            department.setHeadOfDepartment(request.getHeadOfDepartment());
        }

        Department updated = departmentRepository.save(department);
        log.info("Department ID: {} updated successfully", updated.getId());
        return DepartmentResponse.fromEntity(updated);
    }

    @Override
    public void deleteDepartment(String companyId, String departmentId, Long authenticatedUserId, String role) {
        log.info("Deleting department ID: {} in company ID: {}", departmentId, companyId);

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        verifyOwnershipAndRole(company, authenticatedUserId, role);

        Department department = departmentRepository.findByIdAndCompanyId(departmentId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId + " in company: " + companyId));

        // Detach department assignment from recruiters in this department
        List<Recruiter> recruiters = recruiterRepository.findByDepartmentId(departmentId);
        for (Recruiter recruiter : recruiters) {
            recruiter.setDepartmentId(null);
            recruiterRepository.save(recruiter);
        }

        departmentRepository.delete(department);
        log.info("Department ID: {} deleted successfully", departmentId);
    }

    private void verifyOwnershipAndRole(Company company, Long authenticatedUserId, String role) {
        if (authenticatedUserId == null) {
            return;
        }

        if (role != null && (role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("ROLE_ADMIN"))) {
            return;
        }

        if (role != null && role.toUpperCase().contains("CANDIDATE")) {
            throw new UnauthorizedException("Candidates are not authorized to manage company departments");
        }

        if (!company.getUserId().equals(authenticatedUserId)) {
            log.warn("Ownership violation: User ID {} attempted to modify Department in Company ID {} owned by User ID {}",
                    authenticatedUserId, company.getId(), company.getUserId());
            throw new UnauthorizedException("You are not authorized to modify departments for this company");
        }
    }
}
