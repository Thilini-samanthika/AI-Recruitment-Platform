package com.recruitment.company.service;

import com.recruitment.company.dto.*;

import java.util.List;

public interface CompanyService {

    CompanyResponse registerCompany(CreateCompanyRequest request);

    List<CompanyResponse> getAllCompanies();

    CompanyResponse getCompanyById(Long id);

    CompanyResponse getCompanyByUserId(Long userId);

    CompanyResponse updateCompany(Long id, UpdateCompanyRequest request, Long authenticatedUserId, String role);

    void deleteCompany(Long id, Long authenticatedUserId, String role);

    CompanyProfileResponse saveOrUpdateProfile(Long companyId, CompanyProfileRequest request, Long authenticatedUserId, String role);

    CompanyProfileResponse getCompanyProfile(Long companyId);
}
