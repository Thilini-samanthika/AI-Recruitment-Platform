package com.recruitment.company.service;

import com.recruitment.company.dto.CreateDepartmentRequest;
import com.recruitment.company.dto.DepartmentResponse;
import com.recruitment.company.dto.UpdateDepartmentRequest;

import java.util.List;

public interface DepartmentService {

    DepartmentResponse createDepartment(String companyId, CreateDepartmentRequest request, Long authenticatedUserId, String role);

    List<DepartmentResponse> getDepartmentsByCompanyId(String companyId);

    DepartmentResponse getDepartmentById(String companyId, String departmentId);

    DepartmentResponse updateDepartment(String companyId, String departmentId, UpdateDepartmentRequest request, Long authenticatedUserId, String role);

    void deleteDepartment(String companyId, String departmentId, Long authenticatedUserId, String role);
}
