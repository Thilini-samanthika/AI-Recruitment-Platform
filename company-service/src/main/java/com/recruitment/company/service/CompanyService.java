package com.recruitment.company.service;

import com.recruitment.company.dto.*;

import java.util.List;

public interface CompanyService {

    CompanyResponse registerCompany(CreateCompanyRequest request, Long authenticatedUserId, String role);

    List<CompanyResponse> getAllCompanies();

    CompanyResponse getCompanyById(String id);

    CompanyResponse getCompanyByUserId(Long userId);

    CompanyResponse updateCompany(String id, UpdateCompanyRequest request, Long authenticatedUserId, String role);

    void deleteCompany(String id, Long authenticatedUserId, String role);

    CompanyProfileResponse saveOrUpdateProfile(String companyId, CompanyProfileRequest request, Long authenticatedUserId, String role);

    CompanyProfileResponse getCompanyProfile(String companyId);

    VerificationResponse submitVerification(String companyId, SubmitVerificationRequest request, Long authenticatedUserId, String role);

    VerificationResponse reviewVerification(String companyId, ReviewVerificationRequest request, String adminRole, String adminUserId);

    VerificationResponse getVerificationStatus(String companyId, Long authenticatedUserId, String role);
}
