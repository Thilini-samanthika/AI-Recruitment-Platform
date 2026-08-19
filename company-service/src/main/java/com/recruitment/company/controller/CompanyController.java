package com.recruitment.company.controller;

import com.recruitment.company.dto.*;
import com.recruitment.company.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(name = "Company Management", description = "Endpoints for company registration, profiles, directory listing, verification, and updates")
public class CompanyController {

    private final CompanyService companyService;

    // ==========================================
    // 1. COMPANY REGISTRATION & DIRECTORY
    // ==========================================

    @PostMapping
    @Operation(
            summary = "Register a new company",
            description = "Creates a new corporate record. Requires role ROLE_COMPANY or ROLE_ADMIN (forwarded via X-User-Role header by API Gateway) or a valid X-API-KEY header.",
            security = { @SecurityRequirement(name = "apiKeyAuth"), @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Company registered successfully",
                    content = @Content(schema = @Schema(implementation = CompanyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed / Invalid request body",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid API key / JWT authentication",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Candidate users cannot register companies",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "409", description = "Company with email or user ID already exists",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.recruitment.company.dto.ApiResponse<CompanyResponse>> registerCompany(
            @Valid @RequestBody CreateCompanyRequest request,
            @Parameter(description = "Authenticated User ID forwarded by Gateway", hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Parameter(description = "User Role (e.g. ROLE_COMPANY, ROLE_ADMIN)", hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.info("REST request to register company: {}", request.getCompanyName());
        CompanyResponse response = companyService.registerCompany(request, userId, role);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.recruitment.company.dto.ApiResponse.success("Company registered successfully", response));
    }

    @GetMapping
    @Operation(
            summary = "List all companies",
            description = "Retrieves a public directory list of all registered corporate accounts with embedded profiles."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Companies retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CompanyResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.recruitment.company.dto.ApiResponse<List<CompanyResponse>>> getAllCompanies() {
        log.debug("REST request to list all companies");
        List<CompanyResponse> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(com.recruitment.company.dto.ApiResponse.success("Companies retrieved successfully", companies));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get company by ID",
            description = "Retrieves company account details, embedded profile, and verification state by MongoDB ObjectId."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company found",
                    content = @Content(schema = @Schema(implementation = CompanyResponse.class))),
            @ApiResponse(responseCode = "404", description = "Company not found",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.recruitment.company.dto.ApiResponse<CompanyResponse>> getCompanyById(
            @Parameter(description = "Company MongoDB ObjectId", required = true, example = "66c25a1f2b3e8c0012345678")
            @PathVariable String id) {
        log.debug("REST request to get company by ID: {}", id);
        CompanyResponse response = companyService.getCompanyById(id);
        return ResponseEntity.ok(com.recruitment.company.dto.ApiResponse.success("Company retrieved successfully", response));
    }

    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Get company by User ID",
            description = "Retrieves company account linked to the specified auth-service user ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company found for user",
                    content = @Content(schema = @Schema(implementation = CompanyResponse.class))),
            @ApiResponse(responseCode = "404", description = "Company not found for user ID",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.recruitment.company.dto.ApiResponse<CompanyResponse>> getCompanyByUserId(
            @Parameter(description = "Auth Service User ID", required = true, example = "10")
            @PathVariable Long userId) {
        log.debug("REST request to get company by User ID: {}", userId);
        CompanyResponse response = companyService.getCompanyByUserId(userId);
        return ResponseEntity.ok(com.recruitment.company.dto.ApiResponse.success("Company retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update company details",
            description = "Updates company information (name, phone, address). Protected: requires owning ROLE_COMPANY or ROLE_ADMIN.",
            security = { @SecurityRequirement(name = "apiKeyAuth"), @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company updated successfully",
                    content = @Content(schema = @Schema(implementation = CompanyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Caller does not own this company record or has invalid role",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Company not found",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.recruitment.company.dto.ApiResponse<CompanyResponse>> updateCompany(
            @Parameter(description = "Company MongoDB ObjectId", required = true, example = "66c25a1f2b3e8c0012345678")
            @PathVariable String id,
            @Valid @RequestBody UpdateCompanyRequest request,
            @Parameter(description = "Authenticated User ID forwarded by Gateway", hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Parameter(description = "User Role (e.g. ROLE_COMPANY, ROLE_ADMIN)", hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.info("REST request to update company ID: {}", id);
        CompanyResponse response = companyService.updateCompany(id, request, userId, role);
        return ResponseEntity.ok(com.recruitment.company.dto.ApiResponse.success("Company updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a company",
            description = "Deletes a company account and cascades deletion to all associated departments and recruiters. Protected: requires owning ROLE_COMPANY or ROLE_ADMIN.",
            security = { @SecurityRequirement(name = "apiKeyAuth"), @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company deleted successfully",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Caller does not own this company record or has invalid role",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Company not found",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.recruitment.company.dto.ApiResponse<Void>> deleteCompany(
            @Parameter(description = "Company MongoDB ObjectId", required = true, example = "66c25a1f2b3e8c0012345678")
            @PathVariable String id,
            @Parameter(description = "Authenticated User ID forwarded by Gateway", hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Parameter(description = "User Role (e.g. ROLE_COMPANY, ROLE_ADMIN)", hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.info("REST request to delete company ID: {}", id);
        companyService.deleteCompany(id, userId, role);
        return ResponseEntity.ok(com.recruitment.company.dto.ApiResponse.success("Company deleted successfully", null));
    }

    // ==========================================
    // 2. EXTENDED PROFILE ENDPOINTS
    // ==========================================

    @PostMapping("/{id}/profile")
    @Operation(
            summary = "Create or update company profile",
            description = "Sets or updates extended profile details (industry, size, website, description, logo). Protected: requires owning ROLE_COMPANY or ROLE_ADMIN.",
            security = { @SecurityRequirement(name = "apiKeyAuth"), @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company profile saved successfully",
                    content = @Content(schema = @Schema(implementation = CompanyProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed / Invalid request body",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Caller does not own this company record",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Company not found",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.recruitment.company.dto.ApiResponse<CompanyProfileResponse>> saveOrUpdateProfile(
            @Parameter(description = "Company MongoDB ObjectId", required = true, example = "66c25a1f2b3e8c0012345678")
            @PathVariable String id,
            @Valid @RequestBody CompanyProfileRequest request,
            @Parameter(description = "Authenticated User ID forwarded by Gateway", hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Parameter(description = "User Role (e.g. ROLE_COMPANY, ROLE_ADMIN)", hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.info("REST request to update profile for company ID: {}", id);
        CompanyProfileResponse response = companyService.saveOrUpdateProfile(id, request, userId, role);
        return ResponseEntity.ok(com.recruitment.company.dto.ApiResponse.success("Company profile saved successfully", response));
    }

    @GetMapping("/{id}/profile")
    @Operation(
            summary = "Get full company profile",
            description = "Retrieves the extended profile details for a given company ID. Public / Authenticated."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CompanyProfileResponse.class))),
            @ApiResponse(responseCode = "404", description = "Company or profile not found",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.recruitment.company.dto.ApiResponse<CompanyProfileResponse>> getCompanyProfile(
            @Parameter(description = "Company MongoDB ObjectId", required = true, example = "66c25a1f2b3e8c0012345678")
            @PathVariable String id) {
        log.debug("REST request to get profile for company ID: {}", id);
        CompanyProfileResponse response = companyService.getCompanyProfile(id);
        return ResponseEntity.ok(com.recruitment.company.dto.ApiResponse.success("Company profile retrieved successfully", response));
    }

    // ==========================================
    // 3. COMPANY VERIFICATION ENDPOINTS
    // ==========================================

    @PostMapping("/{id}/verification")
    @Operation(
            summary = "Submit company verification documents",
            description = "Submits corporate legal credentials (tax ID, registration number, document URL). Sets status to PENDING. Protected: requires owning ROLE_COMPANY.",
            security = { @SecurityRequirement(name = "apiKeyAuth"), @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification submitted successfully",
                    content = @Content(schema = @Schema(implementation = VerificationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed / Missing required fields",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing authentication",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Caller does not own this company",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Company not found",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.recruitment.company.dto.ApiResponse<VerificationResponse>> submitVerification(
            @Parameter(description = "Company MongoDB ObjectId", required = true, example = "66c25a1f2b3e8c0012345678")
            @PathVariable String id,
            @Valid @RequestBody SubmitVerificationRequest request,
            @Parameter(description = "Authenticated User ID forwarded by Gateway", hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Parameter(description = "User Role (e.g. ROLE_COMPANY, ROLE_ADMIN)", hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.info("REST request to submit verification for company ID: {}", id);
        VerificationResponse response = companyService.submitVerification(id, request, userId, role);
        return ResponseEntity.ok(com.recruitment.company.dto.ApiResponse.success("Verification request submitted successfully", response));
    }

    @PutMapping("/{id}/verification/status")
    @Operation(
            summary = "Review and update company verification status",
            description = "Allows an Administrator to approve (VERIFIED) or reject (REJECTED) a company's verification request. Protected: requires ROLE_ADMIN.",
            security = { @SecurityRequirement(name = "apiKeyAuth"), @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification status updated successfully",
                    content = @Content(schema = @Schema(implementation = VerificationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request status",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing authentication",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Only ROLE_ADMIN can review verifications",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Company not found",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.recruitment.company.dto.ApiResponse<VerificationResponse>> reviewVerification(
            @Parameter(description = "Company MongoDB ObjectId", required = true, example = "66c25a1f2b3e8c0012345678")
            @PathVariable String id,
            @Valid @RequestBody ReviewVerificationRequest request,
            @Parameter(description = "Admin User Role", hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String adminRole,
            @Parameter(description = "Admin User ID", hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) String adminUserId) {
        log.info("REST request to review verification for company ID: {} by admin: {}", id, adminUserId);
        VerificationResponse response = companyService.reviewVerification(id, request, adminRole, adminUserId);
        return ResponseEntity.ok(com.recruitment.company.dto.ApiResponse.success("Verification status updated successfully", response));
    }

    @GetMapping("/{id}/verification")
    @Operation(
            summary = "Get company verification status",
            description = "Retrieves current corporate verification status and review history. Protected: requires owning ROLE_COMPANY or ROLE_ADMIN.",
            security = { @SecurityRequirement(name = "apiKeyAuth"), @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification status retrieved successfully",
                    content = @Content(schema = @Schema(implementation = VerificationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing authentication",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Access denied",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Company not found",
                    content = @Content(schema = @Schema(implementation = com.recruitment.company.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.recruitment.company.dto.ApiResponse<VerificationResponse>> getVerificationStatus(
            @Parameter(description = "Company MongoDB ObjectId", required = true, example = "66c25a1f2b3e8c0012345678")
            @PathVariable String id,
            @Parameter(description = "Authenticated User ID forwarded by Gateway", hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Parameter(description = "User Role (e.g. ROLE_COMPANY, ROLE_ADMIN)", hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.debug("REST request to get verification status for company ID: {}", id);
        VerificationResponse response = companyService.getVerificationStatus(id, userId, role);
        return ResponseEntity.ok(com.recruitment.company.dto.ApiResponse.success("Verification details retrieved successfully", response));
    }
}
