package com.recruitment.company.service;

import com.recruitment.company.dto.CreateRecruiterRequest;
import com.recruitment.company.dto.RecruiterResponse;
import com.recruitment.company.dto.UpdateRecruiterRequest;

import java.util.List;

public interface RecruiterService {

    RecruiterResponse createRecruiter(String companyId, CreateRecruiterRequest request, Long authenticatedUserId, String role);

    List<RecruiterResponse> getRecruitersByCompanyId(String companyId);

    List<RecruiterResponse> getRecruitersByDepartmentId(String companyId, String departmentId);

    RecruiterResponse getRecruiterById(String companyId, String recruiterId);

    RecruiterResponse updateRecruiter(String companyId, String recruiterId, UpdateRecruiterRequest request, Long authenticatedUserId, String role);

    void deleteRecruiter(String companyId, String recruiterId, Long authenticatedUserId, String role);
}
