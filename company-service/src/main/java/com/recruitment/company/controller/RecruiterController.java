package com.recruitment.company.controller;

import com.recruitment.company.dto.ApiResponse;
import com.recruitment.company.dto.CreateRecruiterRequest;
import com.recruitment.company.dto.RecruiterResponse;
import com.recruitment.company.dto.UpdateRecruiterRequest;
import com.recruitment.company.service.RecruiterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/api/companies/{companyId}/recruiters")
@RequiredArgsConstructor
@Tag(name = "Recruiter Management", description = "Endpoints for managing corporate recruiter assignments and contact directory")
public class RecruiterController {

    private final RecruiterService recruiterService;

    @PostMapping
    @Operation(
            summary = "Add recruiter to company",
            description = "Registers a recruiter under the company directory. Protected: requires owning ROLE_COMPANY or ROLE_ADMIN.",
            security = { @SecurityRequirement(name = "apiKeyAuth"), @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Recruiter added successfully",
                    content = @Content(schema = @Schema(implementation = RecruiterResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed / Missing required fields",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Missing authentication",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Caller does not own this company",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Company or department not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Recruiter email already exists in this company",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<RecruiterResponse>> createRecruiter(
            @Parameter(description = "Company MongoDB ObjectId", required = true, example = "66c25a1f2b3e8c0012345678")
            @PathVariable String companyId,
            @Valid @RequestBody CreateRecruiterRequest request,
            @Parameter(description = "Authenticated User ID forwarded by Gateway", hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Parameter(description = "User Role (e.g. ROLE_COMPANY, ROLE_ADMIN)", hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.info("REST request to add recruiter '{}' to company ID: {}", request.getFullName(), companyId);
        RecruiterResponse response = recruiterService.createRecruiter(companyId, request, userId, role);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Recruiter added successfully", response));
    }

    @GetMapping
    @Operation(
            summary = "List recruiters for a company",
            description = "Retrieves all active and inactive recruiters assigned to the company. Public / Authenticated."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Recruiters retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RecruiterResponse.class)))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Company not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<List<RecruiterResponse>>> getRecruiters(
            @Parameter(description = "Company MongoDB ObjectId", required = true, example = "66c25a1f2b3e8c0012345678")
            @PathVariable String companyId) {
        log.debug("REST request to list recruiters for company ID: {}", companyId);
        List<RecruiterResponse> recruiters = recruiterService.getRecruitersByCompanyId(companyId);
        return ResponseEntity.ok(ApiResponse.success("Recruiters retrieved successfully", recruiters));
    }

    @GetMapping("/{recruiterId}")
    @Operation(
            summary = "Get recruiter by ID",
            description = "Retrieves profile and assignment details of a specific recruiter. Public / Authenticated."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Recruiter found",
                    content = @Content(schema = @Schema(implementation = RecruiterResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Recruiter or company not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<RecruiterResponse>> getRecruiterById(
            @Parameter(description = "Company MongoDB ObjectId", required = true, example = "66c25a1f2b3e8c0012345678")
            @PathVariable String companyId,
            @Parameter(description = "Recruiter MongoDB ObjectId", required = true, example = "66c25a1f2b3e8c0012345680")
            @PathVariable String recruiterId) {
        log.debug("REST request to get recruiter ID: {} in company ID: {}", recruiterId, companyId);
        RecruiterResponse response = recruiterService.getRecruiterById(companyId, recruiterId);
        return ResponseEntity.ok(ApiResponse.success("Recruiter retrieved successfully", response));
    }

    @PutMapping("/{recruiterId}")
    @Operation(
            summary = "Update recruiter details",
            description = "Updates recruiter contact info, title, department assignment, or status. Protected: requires owning ROLE_COMPANY or ROLE_ADMIN.",
            security = { @SecurityRequirement(name = "apiKeyAuth"), @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Recruiter updated successfully",
                    content = @Content(schema = @Schema(implementation = RecruiterResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Missing authentication",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Access denied",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Recruiter, department, or company not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email already in use",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<RecruiterResponse>> updateRecruiter(
            @Parameter(description = "Company MongoDB ObjectId", required = true, example = "66c25a1f2b3e8c0012345678")
            @PathVariable String companyId,
            @Parameter(description = "Recruiter MongoDB ObjectId", required = true, example = "66c25a1f2b3e8c0012345680")
            @PathVariable String recruiterId,
            @Valid @RequestBody UpdateRecruiterRequest request,
            @Parameter(description = "Authenticated User ID forwarded by Gateway", hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Parameter(description = "User Role (e.g. ROLE_COMPANY, ROLE_ADMIN)", hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.info("REST request to update recruiter ID: {} in company ID: {}", recruiterId, companyId);
        RecruiterResponse response = recruiterService.updateRecruiter(companyId, recruiterId, request, userId, role);
        return ResponseEntity.ok(ApiResponse.success("Recruiter updated successfully", response));
    }

    @DeleteMapping("/{recruiterId}")
    @Operation(
            summary = "Remove recruiter from company",
            description = "Deletes a recruiter record from the company directory. Protected: requires owning ROLE_COMPANY or ROLE_ADMIN.",
            security = { @SecurityRequirement(name = "apiKeyAuth"), @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Recruiter deleted successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Missing authentication",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Access denied",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Recruiter or company not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<Void>> deleteRecruiter(
            @Parameter(description = "Company MongoDB ObjectId", required = true, example = "66c25a1f2b3e8c0012345678")
            @PathVariable String companyId,
            @Parameter(description = "Recruiter MongoDB ObjectId", required = true, example = "66c25a1f2b3e8c0012345680")
            @PathVariable String recruiterId,
            @Parameter(description = "Authenticated User ID forwarded by Gateway", hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Parameter(description = "User Role (e.g. ROLE_COMPANY, ROLE_ADMIN)", hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.info("REST request to delete recruiter ID: {} in company ID: {}", recruiterId, companyId);
        recruiterService.deleteRecruiter(companyId, recruiterId, userId, role);
        return ResponseEntity.ok(ApiResponse.success("Recruiter deleted successfully", null));
    }
}
