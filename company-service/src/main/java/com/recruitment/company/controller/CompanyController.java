package com.recruitment.company.controller;

import com.recruitment.company.dto.*;
import com.recruitment.company.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Tag(name = "Company Management", description = "Endpoints for company registration, profiles, directory listing, and updates")
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    @Operation(summary = "Register a new company", description = "Creates a new corporate record for an authenticated user with role COMPANY")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Company registered successfully",
                    content = @Content(schema = @Schema(implementation = CompanyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed / Invalid request body"),
            @ApiResponse(responseCode = "409", description = "Company with email or user ID already exists")
    })
    public ResponseEntity<com.recruitment.company.dto.ApiResponse<CompanyResponse>> registerCompany(
            @Valid @RequestBody CreateCompanyRequest request) {
        log.info("REST request to register company: {}", request.getCompanyName());
        CompanyResponse response = companyService.registerCompany(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.recruitment.company.dto.ApiResponse.success("Company registered successfully", response));
    }

    @GetMapping
    @Operation(summary = "List all companies", description = "Retrieves a directory list of all registered corporate accounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Companies retrieved successfully")
    })
    public ResponseEntity<com.recruitment.company.dto.ApiResponse<List<CompanyResponse>>> getAllCompanies() {
        log.debug("REST request to list all companies");
        List<CompanyResponse> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(com.recruitment.company.dto.ApiResponse.success("Companies retrieved successfully", companies));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get company by ID", description = "Retrieves company account details and profile by company primary key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company found"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<com.recruitment.company.dto.ApiResponse<CompanyResponse>> getCompanyById(
            @Parameter(description = "Company ID", required = true) @PathVariable Long id) {
        log.debug("REST request to get company by ID: {}", id);
        CompanyResponse response = companyService.getCompanyById(id);
        return ResponseEntity.ok(com.recruitment.company.dto.ApiResponse.success("Company retrieved successfully", response));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get company by User ID", description = "Retrieves company account linked to the specified auth-service user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company found for user"),
            @ApiResponse(responseCode = "404", description = "Company not found for user ID")
    })
    public ResponseEntity<com.recruitment.company.dto.ApiResponse<CompanyResponse>> getCompanyByUserId(
            @Parameter(description = "User ID", required = true) @PathVariable Long userId) {
        log.debug("REST request to get company by User ID: {}", userId);
        CompanyResponse response = companyService.getCompanyByUserId(userId);
        return ResponseEntity.ok(com.recruitment.company.dto.ApiResponse.success("Company retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update company details", description = "Updates company information (name, phone, address)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company updated successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Caller does not own this company record"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<com.recruitment.company.dto.ApiResponse<CompanyResponse>> updateCompany(
            @Parameter(description = "Company ID", required = true) @PathVariable Long id,
            @Valid @RequestBody UpdateCompanyRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.info("REST request to update company ID: {}", id);
        CompanyResponse response = companyService.updateCompany(id, request, userId, role);
        return ResponseEntity.ok(com.recruitment.company.dto.ApiResponse.success("Company updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a company", description = "Deletes a company account and all associated profile details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Caller does not own this company record"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<com.recruitment.company.dto.ApiResponse<Void>> deleteCompany(
            @Parameter(description = "Company ID", required = true) @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.info("REST request to delete company ID: {}", id);
        companyService.deleteCompany(id, userId, role);
        return ResponseEntity.ok(com.recruitment.company.dto.ApiResponse.success("Company deleted successfully", null));
    }

    @PostMapping("/{id}/profile")
    @Operation(summary = "Create or update company profile", description = "Sets or updates extended profile details (industry, size, website, about, logo)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company profile saved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Caller does not own this company record"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<com.recruitment.company.dto.ApiResponse<CompanyProfileResponse>> saveOrUpdateProfile(
            @Parameter(description = "Company ID", required = true) @PathVariable Long id,
            @Valid @RequestBody CompanyProfileRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.info("REST request to update profile for company ID: {}", id);
        CompanyProfileResponse response = companyService.saveOrUpdateProfile(id, request, userId, role);
        return ResponseEntity.ok(com.recruitment.company.dto.ApiResponse.success("Company profile saved successfully", response));
    }

    @GetMapping("/{id}/profile")
    @Operation(summary = "Get full company profile", description = "Retrieves the extended profile details for a given company ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company profile retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Company or profile not found")
    })
    public ResponseEntity<com.recruitment.company.dto.ApiResponse<CompanyProfileResponse>> getCompanyProfile(
            @Parameter(description = "Company ID", required = true) @PathVariable Long id) {
        log.debug("REST request to get profile for company ID: {}", id);
        CompanyProfileResponse response = companyService.getCompanyProfile(id);
        return ResponseEntity.ok(com.recruitment.company.dto.ApiResponse.success("Company profile retrieved successfully", response));
    }
}
